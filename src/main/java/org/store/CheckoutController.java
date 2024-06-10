package org.store;

import database.repository.OrderItemRepo;
import database.repository.OrderRepo;
import database.repository.ProductRepo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.oorsprong.websamples.TCountryCodeAndName;
import org.store.enumeration.OrderStatus;
import org.store.utils.CryptoKey;
import org.store.model.Order;
import org.store.model.OrderItem;
import org.store.model.OrderItemDisplay;
import org.store.utils.KeyManager;
import org.store.utils.OrderJsonUtils;
import org.store.utils.UserSession;
import org.oorsprong.websamples.CountryInfoService;
import org.oorsprong.websamples.CountryInfoServiceSoapType;


import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CheckoutController {

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
    private TextField addressField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField cardholderNameField;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField expDayField;

    @FXML
    private TextField expMonthField;

    @FXML
    private TextField cvcField;

    @FXML
    private ChoiceBox<String> countryChoiceBox;

    @FXML
    private Label addressErrorLabel;

    @FXML
    private Label postalCodeErrorLabel;

    @FXML
    private Label phoneNumberErrorLabel;

    @FXML
    private Label cardholderNameErrorLabel;

    @FXML
    private Label cardNumberErrorLabel;

    @FXML
    private Label expDayErrorLabel;

    @FXML
    private Label expMonthErrorLabel;

    @FXML
    private Label cvcErrorLabel;

    @FXML
    private Label countryErrorLabel;

    @FXML
    private Button updateOrderButton;

    private final ObservableList<OrderItemDisplay> orderItems = FXCollections.observableArrayList();
    private Order currentOrder;

    @FXML
    public void initialize() throws SQLException, IOException {
        String userEmail = UserSession.getInstance().getUser().getEmail();
        currentOrder = OrderRepo.getOrderInProgressByEmail(userEmail);
        if (currentOrder == null) {
            Main.getMainApp().showMainView();
            return;
        }

        // Initialize the table columns
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        columnTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        fetchOrderItemsFromDatabase();
        populateCountryChoiceBox();
        cartTableView.setItems(orderItems);
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

    private void populateCountryChoiceBox() {
        CountryInfoService service = new CountryInfoService();
        CountryInfoServiceSoapType port = service.getCountryInfoServiceSoap();
        List<TCountryCodeAndName> countryList = port.listOfCountryNamesByName().getTCountryCodeAndName();

        List<String> countryNames = countryList.stream()
                .map(TCountryCodeAndName::getSName)
                .collect(Collectors.toList());

        countryChoiceBox.setItems(FXCollections.observableArrayList(countryNames));
    }

    @FXML
    private void handleUpdateOrder() throws Exception {
        String userEmail = UserSession.getInstance().getUser().getEmail();
        currentOrder = OrderRepo.getOrderInProgressByEmail(userEmail);
        if (currentOrder == null) {
            Main.getMainApp().showMainView();
            return;
        }

        boolean isValid = validateFields();

        KeyManager keyManager = new KeyManager();
        if (isValid) {
            CryptoKey cryptoKey = keyManager.generateAndSaveKeys(currentOrder.getId());

            String cardInformation = cardholderNameField.getText() + "-" + cardNumberField.getText() + "-" +
                                     expDayField.getText() + "-" + expMonthField.getText() + "-" + cvcField.getText();
            String encryptedCardInformation = keyManager.encryptWithRSA(cryptoKey.getRsaPublicKey(), cardInformation);

            String userAddress = addressField.getText() + ", " + postalCodeField.getText() + ", " + countryChoiceBox.getValue();
            String encryptedUserAddress = keyManager.encryptWithAES(cryptoKey.getAesKey(), userAddress);
            String encryptedPhoneNumber = keyManager.encryptWithAES(cryptoKey.getAesKey(), phoneNumberField.getText());
            String encryptedEmail = keyManager.encryptWithAES(cryptoKey.getAesKey(), currentOrder.getEmail());

            currentOrder.setAddress(encryptedUserAddress);
            currentOrder.setPhoneNumber(encryptedPhoneNumber);
            currentOrder.setDate(Timestamp.valueOf(LocalDateTime.now()));
            currentOrder.setEmail(encryptedEmail);
            currentOrder.setCardNumber(encryptedCardInformation);
            currentOrder.setStatus(OrderStatus.COMPLETED);
            currentOrder.setUserId(UserSession.getInstance().getUser().getId());

            OrderRepo.updateOrder(currentOrder);
            OrderJsonUtils.addOrderToFile(currentOrder);
            showAutoCloseDialog(((Stage) updateOrderButton.getScene().getWindow()));
        }
    }

    private void showAutoCloseDialog(Stage owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank You!");
        alert.setHeaderText(null);
        alert.setContentText("Your order will be arriving soon.");

        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/alert.css")).toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        new Thread(() -> {
            try {
                Thread.sleep(3500);
                if (alert.isShowing()) {
                    Platform.runLater(() -> {
                        alert.close();
                        try {
                            Main.getMainApp().showMainView();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        alert.initOwner(owner);
        alert.show();
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (addressField.getText().isEmpty()) {
            addressErrorLabel.setText("Address is required.");
            isValid = false;
        } else {
            addressErrorLabel.setText("");
        }

        if (postalCodeField.getText().isEmpty()) {
            postalCodeErrorLabel.setText("Postal code is required.");
            isValid = false;
        } else {
            postalCodeErrorLabel.setText("");
        }

        if (phoneNumberField.getText().isEmpty() || !Pattern.matches("\\d{10}", phoneNumberField.getText())) {
            phoneNumberErrorLabel.setText("Invalid phone number.");
            isValid = false;
        } else {
            phoneNumberErrorLabel.setText("");
        }

        if (cardholderNameField.getText().isEmpty()) {
            cardholderNameErrorLabel.setText("Cardholder name is required.");
            isValid = false;
        } else {
            cardholderNameErrorLabel.setText("");
        }

        if (cardNumberField.getText().isEmpty() || !Pattern.matches("\\d{16}", cardNumberField.getText())) {
            cardNumberErrorLabel.setText("Valid card number is required.");
            isValid = false;
        } else {
            cardNumberErrorLabel.setText("");
        }

        if (expDayField.getText().isEmpty() || !Pattern.matches("\\d{2}", expDayField.getText())) {
            expDayErrorLabel.setText("Invalid entry.");
            isValid = false;
        } else {
            expDayErrorLabel.setText("");
        }

        if (expMonthField.getText().isEmpty() || !Pattern.matches("\\d{2}", expMonthField.getText())) {
            expMonthErrorLabel.setText("Invalid entry.");
            isValid = false;
        } else {
            expMonthErrorLabel.setText("");
        }

        if (cvcField.getText().isEmpty() || !Pattern.matches("\\d{3}", cvcField.getText())) {
            cvcErrorLabel.setText("Invalid entry.");
            isValid = false;
        } else {
            cvcErrorLabel.setText("");
        }

        if (countryChoiceBox.getValue() == null || countryChoiceBox.getValue().isEmpty()) {
            countryErrorLabel.setText("Country required.");
            isValid = false;
        } else {
            countryErrorLabel.setText("");
        }

        return isValid;
    }
}
