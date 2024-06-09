package org.store;

import database.repository.OrderRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.store.model.Order;
import org.store.model.OrderDisplay;
import org.store.model.OrderItem;
import org.store.model.OrderItemDisplay;
import org.store.utils.OrderJsonUtils;
import org.store.utils.PdfUtils;
import org.store.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
    private TableView<OrderItem> orderItemsTableView;

    @FXML
    private TableColumn<OrderItem, String> itemNameColumn;

    @FXML
    private TableColumn<OrderItem, Integer> itemAmountColumn;

    @FXML
    private TableColumn<OrderItem, Double> itemPriceColumn;

    private OrderDisplay selectedOrderDisplay;
    private final ObservableList<OrderDisplay> orderDisplays = FXCollections.observableArrayList();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        // Initialize columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        productsColumn.setCellValueFactory(new PropertyValueFactory<>("products"));

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Populate download speed choices
        downloadSpeedChoiceBox.getItems().addAll("Slow", "Medium", "Fast");
        downloadSpeedChoiceBox.setValue("Fast");

        // Add listener for order selection
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrderDisplay = newSelection;
                displayOrderDetails(selectedOrderDisplay);
            }
        });

        // Add listener for download PDF button
        downloadPdfButton.setOnAction(event -> downloadOrderAsPdf());
    }

    private void loadOrders() throws SQLException, IOException {
        int userId = UserSession.getInstance().getUser().getId();
        List<Order> orders = OrderJsonUtils.getOrdersByUserId(userId);
        for (Order order : orders) {
            BigDecimal totalPrice = OrderRepo.getTotalPrice(order.getId());
            String products = order.getOrderItems().stream()
                    .map(item -> item.getProduct().getTitle() + " x" + item.getQuantity())
                    .collect(Collectors.joining(", "));

            String formattedDate = order.getDate().format(formatter);

            OrderDisplay orderDisplay = new OrderDisplay(
                    formattedDate,
                    order.getStatus().name(),
                    totalPrice,
                    products,
                    order.getId()
            );

            orderDisplays.add(orderDisplay);
        }
    }

    private void displayOrderDetails(OrderDisplay OrderDisplay) {
        customerLabel.setText(order.getCustomerName());
        emailLabel.setText(order.getEmail());
        addressLabel.setText(order.getAddress());
        cardNumberLabel.setText(order.getCardNumber());
        dateLabel.setText(order.getDate().toString());
        totalPriceLabel.setText(String.valueOf(order.getTotalPrice()));

        orderItemsTableView.setItems(order.getOrderItems());
    }

    private void downloadOrderAsPdf() {

    }

    private int getDownloadSpeed() {
        String speed = downloadSpeedChoiceBox.getValue();
        switch (speed) {
            case "Slow":
                return 512; // KB/s
            case "Medium":
                return 1024; // KB/s
            case "Fast":
            default:
                return 2048; // KB/s
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}