import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.store.model.Order;
import org.store.model.OrderItemDisplay;
import org.store.model.Product;
import org.store.utils.ApiService;

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
    private TextField quantityTextField;

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

    private final ObservableList<OrderItemDisplay> orderItems = FXCollections.observableArrayList();

    private int quantity = 1;
    private Order currentOrder;

    private Product selectedProduct;

    private final ApiService productApiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20); // Adjust the pool size as needed


    @FXML
    public void initialize() {
        // Initialize the table columns
        columnId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        columnProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        columnQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));
        columnTotalPrice.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalPrice())));

        // Fetch and populate order items from the database
        fetchOrderItemsFromDatabase();

        // Bind the order items to the table
        cartTableView.setItems(orderItems);

        // Select the first item if available
        if (!orderItems.isEmpty()) {
            cartTableView.getSelectionModel().selectFirst();
            displaySelectedProductDetails(orderItems.get(0));
        }

        // Add listener to table row selection
        cartTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displaySelectedProductDetails(newSelection);
            }
        });
    }

    private void fetchOrderItemsFromDatabase() {
        try {
            // Fetch order items from the database (replace with actual DB call)
            List<OrderItem> items = OrderItemRepository.getAllOrderItems();
            orderItems.setAll(items);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch order items: " + e.getMessage());
        }
    }

    private void displaySelectedProductDetails(OrderItem orderItem) {
        try {
            // Fetch product details from the database (replace with actual DB call)
            Product product = ProductRepository.getProductById(orderItem.getProductId());

            productTitleLabel.setText(product.getTitle());
            productDescriptionLabel.setText(product.getDescription());
            productPriceLabel.setText(String.valueOf(product.getPrice()));
            quantityTextField.setText(String.valueOf(orderItem.getQuantity()));

            if (product.getImage() != null) {
                productImageView.setImage(new Image(new ByteArrayInputStream(product.getImage())));
            } else {
                productImageView.setImage(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch product details: " + e.getMessage());
        }
    }

    private void handleResponse(CloseableHttpResponse response, String successMessage) throws IOException {
        Platform.runLater(() -> {
            try {
                int statusCode = response.getCode();
                if (statusCode == 200 || statusCode == 201) {
                    System.out.println("Success " + successMessage);
                } else if (statusCode == 403) {
                    showAlert("Permission Denied", "You do not have permission to perform this action.");
                } else {
                    System.err.println(response.getCode() + " Request failed.");
                }
                response.close();
            } catch (IOException e) {
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
        if (quantity > 1) {
            quantity--;
            quantityLabel.setText(String.valueOf(quantity));
        }
    }

    @FXML
    void handleIncrement() {
        quantity++;
        quantityLabel.setText(String.valueOf(quantity));
    }
}