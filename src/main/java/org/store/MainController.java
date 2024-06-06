package org.store;

import database.Database;
import database.repository.OrderItemRepo;
import database.repository.ProductRepo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.store.model.Order;
import org.store.model.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private ChoiceBox<String> yearChoiceBox;

    @FXML
    private ChoiceBox<String> brandChoiceBox;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label brandLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private RadioButton sortByAlphabetically;

    @FXML
    private RadioButton sortByPrice;

    @FXML
    private TableView<Product> mainTableView;

    @FXML
    private TableColumn<Product, String> columnId;

    @FXML
    private TableColumn<Product, String> columnTitle;

    @FXML
    private TableColumn<Product, String> columnBrand;

    @FXML
    private TableColumn<Product, String> columnCategory;

    @FXML
    private TableColumn<Product, String> columnYear;

    @FXML
    private TableColumn<Product, String> columnPrice;

    @FXML
    private TextField searchTextField;

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
        columnId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        columnTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        columnBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        columnCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        columnYear.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProductYear())));
        columnPrice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice().toString()));

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

        // Fetch and set items for choice boxes
        try {
            brandChoiceBox.setItems(FXCollections.observableArrayList(getBrandItems()));
            categoryChoiceBox.setItems(FXCollections.observableArrayList(getCategoryItems()));
            yearChoiceBox.setItems(FXCollections.observableArrayList(getYearItems()));

            brandChoiceBox.getSelectionModel().selectFirst();
            categoryChoiceBox.getSelectionModel().selectFirst();
            yearChoiceBox.getSelectionModel().selectFirst();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        // Add listeners to choice boxes
        brandChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        yearChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterProducts());
    }

    // Method to fetch all brands from the database
    private List<String> fetchBrandsFromDatabase() throws SQLException, IOException {
        return ProductRepo.getAllBrands();
    }

    // Method to fetch all categories from the database
    private List<String> fetchCategoriesFromDatabase() throws SQLException, IOException {
        return ProductRepo.getAllCategories();
    }

    // Method to fetch all years from the database
    private List<String> fetchYearsFromDatabase() throws SQLException, IOException {
        return ProductRepo.getAllYears();
    }

    private List<String> getBrandItems() throws SQLException, IOException {
        List<String> brands = new ArrayList<>();
        brands.add("Brand");
        brands.addAll(fetchBrandsFromDatabase());
        return brands;
    }

    private List<String> getCategoryItems() throws SQLException, IOException {
        List<String> categories = new ArrayList<>();
        categories.add("Category");
        categories.addAll(fetchCategoriesFromDatabase());
        return categories;
    }

    private List<String> getYearItems() throws SQLException, IOException {
        List<String> years = new ArrayList<>();
        years.add("Years");
        years.addAll(fetchYearsFromDatabase());
        return years;
    }

    private void filterProducts() {
        String selectedBrand = brandChoiceBox.getSelectionModel().getSelectedItem();
        String selectedCategory = categoryChoiceBox.getSelectionModel().getSelectedItem();
        String selectedYear = yearChoiceBox.getSelectionModel().getSelectedItem();

        if ("Brand".equals(selectedBrand)) {
            selectedBrand = null;
        }
        if ("Category".equals(selectedCategory)) {
            selectedCategory = null;
        }
        if ("Year".equals(selectedCategory)) {
            selectedYear = null;
        }

        // Implement your filtering logic here based on selectedBrand, selectedCategory, and selectedYear
        System.out.println("Filtering products with Brand: " + selectedBrand + ", Category: " + selectedCategory + ", Year: " + selectedYear);
        // Update your table view with filtered products
    }

    private void fetchProductsFromDatabase() {
        try {
            List<Product> productList = ProductRepo.getAllProducts();
            products.setAll(productList);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
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
            OrderItemRepo.addOrderItem(order, mainTableView.getSelectionModel().getSelectedItem(), quantity);
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
