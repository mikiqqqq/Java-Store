package org.store;

import database.repository.OrderItemRepo;
import database.repository.OrderRepo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.store.model.Order;
import org.store.model.OrderItem;
import org.store.model.OrderItemDisplay;
import org.store.model.Product;
import org.store.utils.ApiService;
import org.store.utils.ImageConverter;
import org.store.utils.UserSession;
import database.repository.ProductRepo;
import java.math.BigDecimal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartController {

    @FXML
    private ImageView productImageView;

    @FXML
    private Label productTitleLabel;

    @FXML
    private Label productDescriptionLabel;

    @FXML
    private Label productPriceLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private TableView<OrderItemDisplay> cartTableView;

    @FXML
    private TableColumn<OrderItemDisplay, Integer> columnId;

    @FXML
    private TableColumn<OrderItemDisplay, String> columnProductName;

    @FXML
    private TableColumn<OrderItemDisplay, Integer> columnQuantity;

    @FXML
    private TableColumn<OrderItemDisplay, Double> columnTotalPrice;

    @FXML
    private VBox VBox;

    private final ObservableList<OrderItemDisplay> orderItems = FXCollections.observableArrayList();

    private int quantity = 1;
    private Order currentOrder;
    private OrderItemDisplay selectedOrderItem;

    private Product selectedProduct;

    private final ApiService orderItemApiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed

    public CartController() {
        this.orderItemApiService = new ApiService("http://localhost:8080/api/order-item/");
    }

    @FXML
    public void initialize() throws SQLException, IOException {
        String userEmail = UserSession.getInstance().getUser().getEmail();
        currentOrder = OrderRepo.getOrderInProgressByEmail(userEmail);
        if (currentOrder == null) {
            VBox.setVisible(false);
            return;
        }

        // Initialize the table columns
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        columnTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        updateTable();

        // Select the first item if available
        if (!orderItems.isEmpty()) {
            cartTableView.getSelectionModel().selectFirst();
            selectedOrderItem = cartTableView.getSelectionModel().getSelectedItem();
            quantity = selectedOrderItem.getQuantity();
            displaySelectedProductDetails(orderItems.getFirst());
        }

        // Add listener to table row selection
        cartTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrderItem = newSelection;
                quantity = selectedOrderItem.getQuantity();
                displaySelectedProductDetails(newSelection);
            }
        });
    }

    private void updateTable() throws SQLException, IOException {
        orderItems.clear();

        // Fetch and populate order items from the database
        fetchOrderItemsFromDatabase();

        // Bind the order items to the table
        cartTableView.setItems(orderItems);
    }

    private void displaySelectedProductDetails(OrderItemDisplay orderItemDisplay) {
        try {
            // Fetch product details from the database (replace with actual DB call)
            Product product = ProductRepo.getProductById(orderItemDisplay.getProductId());

            productTitleLabel.setText(product.getTitle());
            productDescriptionLabel.setText(product.getDescription());
            productPriceLabel.setText(String.valueOf(product.getPrice()));
            quantityLabel.setText(String.valueOf(orderItemDisplay.getQuantity()));

            if (product.getImage() != null) {
                productImageView.setImage(ImageConverter.byteArrayToImage(product.getImage()));
            } else {
                productImageView.setImage(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch product details: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchOrderItemsFromDatabase() throws SQLException, IOException {
        List<OrderItem> orderItemList = OrderItemRepo.getOrderItemsByOrderId(currentOrder.getId());

        for (OrderItem orderItem : orderItemList) {
            String productName = ProductRepo.getProductById(orderItem.getItemId()).getTitle();
            BigDecimal price = ProductRepo.getProductById(orderItem.getItemId()).getPrice(); // Assuming this returns BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
            BigDecimal totalPrice = quantity.multiply(price);
            OrderItemDisplay orderItemDisplay = new OrderItemDisplay(orderItem, productName, totalPrice);
            orderItems.add(orderItemDisplay);
        }
    }

    @FXML
    private void removeOrderItem() {
        if(selectedOrderItem == null || currentOrder == null) {
            return;
        }

        executorService.submit(() -> {
            String endpoint = "delete/orderId=" + currentOrder.getId() + "&itemId=" + selectedOrderItem.getProductId();
            try (CloseableHttpResponse response = orderItemApiService.sendRequest(endpoint, null, "DELETE")) {
                handleResponse(response, "Removed order item.", false);
                cartTableView.getSelectionModel().selectFirst();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to remove order item: " + e.getMessage());
            }
        });
    }

    @FXML
    private void removeAllOrderItems() {
        if (currentOrder == null) {
            return;
        }

        executorService.submit(() -> {
            try (CloseableHttpResponse response = orderItemApiService.sendRequest("delete-all/" + currentOrder.getId(), null, "DELETE")) {
                handleResponse(response, "Removed all order items.", true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to add product: " + e.getMessage());
            }
        });
    }

    private void handleResponse(CloseableHttpResponse response, String successMessage, boolean wipe) throws IOException {
        Platform.runLater(() -> {
            try {
                int statusCode = response.getCode();
                if (statusCode == 200 || statusCode == 201) {
                    System.out.println("Success " + successMessage);
                    if(wipe) {
                        OrderRepo.deleteOrderById(currentOrder.getId());
                        VBox.setVisible(false);
                        System.out.println("Order wiped successfully.");
                    }
                } else if (statusCode == 403) {
                    showAlert("Permission Denied", "You do not have permission to perform this action.");
                } else {
                    System.err.println(response.getCode() + " Request failed.");
                }
                updateTable();
                response.close();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleDecrement() {
        if(selectedOrderItem == null) {
            return;
        }

        if (quantity > 1) {
            executorService.submit(() -> {
                OrderItem orderItem = new OrderItem(selectedOrderItem.getId(), --quantity, selectedOrderItem.getOrderId(), selectedOrderItem.getProductId());
                try (CloseableHttpResponse response = orderItemApiService.sendRequest("update", orderItem, "PUT")) {
                    handleResponse(response, "Updated order item.", false);
                    Platform.runLater(() -> {
                        try {
                            quantityLabel.setText(String.valueOf(OrderItemRepo.getQuantityByOrderItemId(orderItem.getId())));
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error: Failed to update order item: " + e.getMessage());
                }
            });
        }
    }

    @FXML
    void handleIncrement() {
        if(selectedOrderItem == null) {
            return;
        }

        executorService.submit(() -> {
            OrderItem orderItem = new OrderItem(selectedOrderItem.getId(), ++quantity, selectedOrderItem.getOrderId(), selectedOrderItem.getProductId());
            try (CloseableHttpResponse response = orderItemApiService.sendRequest("update", orderItem, "PUT")) {
                handleResponse(response, "Updated order item.", false);
                Platform.runLater(() -> {
                    try {
                        quantityLabel.setText(String.valueOf(OrderItemRepo.getQuantityByOrderItemId(orderItem.getId())));
                    } catch (SQLException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to update order item: " + e.getMessage());
            }
        });
    }
}