package org.store;

import database.repository.OrderRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.store.model.*;
import org.store.utils.KeyManager;
import org.store.utils.OrderJsonUtils;

import org.store.utils.UserSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersController {

    @FXML
    private Button downloadPdfButton;

    @FXML
    private ChoiceBox<String> downloadSpeedChoiceBox;

    @FXML
    private TableView<OrderDisplay> ordersTableView;

    @FXML
    private TableColumn<OrderDisplay, String> dateColumn;

    @FXML
    private TableColumn<OrderDisplay, String> statusColumn;

    @FXML
    private TableColumn<OrderDisplay, Double> priceColumn;

    @FXML
    private TableColumn<OrderDisplay, String> productsColumn;

    @FXML
    private Label customerLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label cardNumberLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private TableView<Product> orderItemsTableView;

    @FXML
    private TableColumn<Product, String> itemNameColumn;

    @FXML
    private TableColumn<Product, Integer> itemAmountColumn;

    @FXML
    private TableColumn<Product, Double> itemPriceColumn;

    private OrderDisplay selectedOrderDisplay;
    private final ObservableList<OrderDisplay> orderDisplays = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final KeyManager keyManager = new KeyManager();

    @FXML
    public void initialize() throws SQLException, IOException {
        // Initialize columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productsColumn.setCellValueFactory(new PropertyValueFactory<>("products"));

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Populate download speed choices
        downloadSpeedChoiceBox.getItems().addAll("Slow", "Medium", "Fast");
        downloadSpeedChoiceBox.setValue("Fast");

        loadOrders();

        // Add listener for order selection
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                selectedOrderDisplay = newSelection;
                try {
                    displayOrderDetails(selectedOrderDisplay);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        // Add listener for download PDF button
        downloadPdfButton.setOnAction(_ -> downloadOrderAsPdf());
    }

    private void loadOrders() throws SQLException, IOException {
        int userId = UserSession.getInstance().getUser().getId();
        List<Order> orders = OrderJsonUtils.getOrdersByUserId(userId);
        System.out.println(orders);
        for (Order order : orders) {
            BigDecimal totalPrice = OrderRepo.getTotalPrice(order.getId()).setScale(2, RoundingMode.HALF_UP);
            String products = OrderRepo.getProductsByOrderId(order.getId()).stream()
                    .map(product -> product.getTitle() + " x" + product.getQuantity())
                    .collect(Collectors.joining(", "));

            String formattedDate = order.getDate().toLocalDateTime().format(formatter);

            OrderDisplay orderDisplay = new OrderDisplay(
                    formattedDate,
                    order.getStatus().name(),
                    totalPrice,
                    products,
                    order.getId()
            );

            orderDisplays.add(orderDisplay);
        }
        ordersTableView.setItems(orderDisplays);
    }

    private void displayOrderDetails(OrderDisplay orderDisplay) throws Exception {
        Order order = OrderJsonUtils.getOrderById(orderDisplay.getOrderId());
        customerLabel.setText(UserSession.getInstance().getUser().getFullName());

        CryptoKey cryptoKey = keyManager.getKeyForOrder(order.getId());

        String email = keyManager.decryptWithAES(cryptoKey.getAesKey(), order.getEmail());
        String address = keyManager.decryptWithAES(cryptoKey.getAesKey(), order.getAddress());
        String cardInformation = keyManager.decryptWithRSA(cryptoKey.getRsaPrivateKey(), order.getCardNumber());

        emailLabel.setText(email);
        addressLabel.setText(address);
        cardNumberLabel.setText(cardInformation);
        dateLabel.setText(order.getDate().toLocalDateTime().format(formatter));
        totalPriceLabel.setText(String.valueOf(OrderRepo.getTotalPrice(order.getId())));

        List<Product> productList = OrderRepo.getProductsByOrderId(order.getId());
        products.setAll(productList);
        orderItemsTableView.setItems(products);
    }

    private void downloadOrderAsPdf() {

    }
}