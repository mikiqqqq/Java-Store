package org.store;

import database.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.store.model.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML
    private Button adminButton;

    @FXML
    private Button cartButton;

    @FXML
    private Button contactButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button orderHistoryButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button userInfoButton;

    @FXML
    private Button addToCartButton;

    @FXML
    private Button incrementButton;

    @FXML
    private Button decrementButton;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    @FXML
    private ChoiceBox<String> anotherChoiceBox;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label logoLabel;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private RadioButton sortByAlphabetically;

    @FXML
    private RadioButton sortByPrice;

    @FXML
    private TableColumn<Product, String> column1;

    @FXML
    private TableColumn<Product, String> column2;

    @FXML
    private TableColumn<Product, String> column3;

    @FXML
    private TableView<Product> mainTableView;

    @FXML
    private TextField searchTextField;

    @FXML
    private VBox rightVBox;

    @FXML
    private ImageView mainImageView;

    private final ObservableList<Product> products = FXCollections.observableArrayList();

    private int quantity = 0;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() {
        // Initialize the table columns
        column1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        column2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        column3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice().toString()));

        // Fetch products from the database
        fetchProductsFromDatabase();

        // Bind the products to the table
        mainTableView.setItems(products);

        // Add listener to table selection
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showProductDetails(newSelection);
            }
        });

        // Add listener to search field
        searchTextField.textProperty().addListener((obs, oldText, newText) -> filterProducts());

        // Initialize the choice boxes and radio buttons for filtering and sorting
        filterChoiceBox.setItems(FXCollections.observableArrayList("Filter1", "Filter2", "Filter3"));
        sortChoiceBox.setItems(FXCollections.observableArrayList("Sort1", "Sort2", "Sort3"));
        anotherChoiceBox.setItems(FXCollections.observableArrayList("Choice1", "Choice2", "Choice3"));
    }

    private void fetchProductsFromDatabase() {
        try {
            List<Product> productList = Database.getAllProducts();
            products.setAll(productList);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void filterProducts() {
        // Implement product filtering logic
    }

    private void showProductDetails(Product product) {
        mainImageView.setImage(new Image("path/to/image"));
        titleLabel.setText(product.getTitle());
        descriptionLabel.setText(product.getDescription());
        priceLabel.setText(String.valueOf(product.getPrice()));
        quantity = 0; // Reset quantity
        quantityLabel.setText(String.valueOf(quantity));
    }

    @FXML
    void handleAddToCart() {
        // Handle add to cart logic here
        // Update the order with order items
        try {
            Order order = new Order(); // Assuming you have a method to get the current order
            Database.addOrderItem(order, mainTableView.getSelectionModel().getSelectedItem(), quantity);
            System.out.println("Product added to cart.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAdmin() {
        try {
            mainApp.showAdminView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCart() {
        try {
            mainApp.showCartView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleContact() {
        try {
            mainApp.showContactDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDecrement() {
        if (quantity > 0) {
            quantity--;
            quantityLabel.setText(String.valueOf(quantity));
        }
    }

    @FXML
    void handleIncrement() {
        quantity++;
        quantityLabel.setText(String.valueOf(quantity));
    }

    @FXML
    void handleLogOut() throws IOException {
        mainApp.showLoginView();
    }

    @FXML
    void handleOrderHistory() {
        try {
            mainApp.showOrderHistoryView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSearch() {
        filterProducts();
    }

    @FXML
    void handleSettings() {
        try {
            mainApp.showSettingsDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSortByAlphabetically() {
        // Handle sort by alphabetically logic here
    }

    @FXML
    void handleSortByPrice() {
        // Handle sort by price logic here
    }

    @FXML
    void handleUserInfo() {
        try {
            mainApp.showUserInfoView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
