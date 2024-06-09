package org.store;

import database.repository.OrderItemRepo;
import database.repository.OrderRepo;
import database.repository.ProductRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import org.oorsprong.websamples.CountryInfoService;
import org.oorsprong.websamples.CountryInfoServiceSoapType;
import org.oorsprong.websamples.TCountryCodeAndName;
import org.store.model.Order;
import org.store.model.OrderItem;
import org.store.model.OrderItemDisplay;
import org.store.utils.UserSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
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
    private Button updateOrderButton;

    private final ObservableList<OrderItemDisplay> orderItems = FXCollections.observableArrayList();
    private Order currentOrder;
    private int currentOrderId; // Set this to the current order ID

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
    private void handleUpdateOrder() {
        // Collect data from fields and update the order
        // Example:
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String cardholderName = cardholderNameField.getText();
        String cardNumber = cardNumberField.getText();
        String expDay = expDayField.getText();
        String expMonth = expMonthField.getText();
        String cvc = cvcField.getText();
        String country = countryChoiceBox.getValue();

        // Create or update the order object with collected data
        // ...

        System.out.println("Order updated with address: " + address + ", country: " + country);
    }
}
