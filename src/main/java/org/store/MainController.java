package org.store;

import database.repository.OrderItemRepo;
import database.repository.OrderRepo;
import database.repository.ProductRepo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.store.enumeration.OrderStatus;
import org.store.model.Order;
import org.store.model.Product;
import org.store.utils.ImageConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

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
    private ToggleGroup sortToggleGroup;

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
    private Order currentOrder;

    public void initialize() {
        // Initialize the table columns
        columnId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        columnTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        columnBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        columnCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        columnYear.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProductYear())));
        columnPrice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice().toString()));

        displayProductDetails(1);

        // Fetch products from the database
        fetchProductsFromDatabase();

        // Bind the products to the table
        mainTableView.setItems(products);

        // Add listener to table selection
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayProductDetails(newSelection.getId());
            }
        });

        // Add listener to search field
        searchTextField.textProperty().addListener((obs, oldText, newText) -> filterAndSortProducts());

        // Add listener to sort toggle group for sorting
        sortToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> filterAndSortProducts());

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
        brandChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterAndSortProducts());
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterAndSortProducts());
        yearChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterAndSortProducts());
    }

    private void filterAndSortProducts() {
        String brand = brandChoiceBox.getSelectionModel().getSelectedItem().equals("Brand") ? null : brandChoiceBox.getSelectionModel().getSelectedItem();
        String category = categoryChoiceBox.getSelectionModel().getSelectedItem().equals("Category") ? null : categoryChoiceBox.getSelectionModel().getSelectedItem();
        String year = yearChoiceBox.getSelectionModel().getSelectedItem().equals("Year") ? null : yearChoiceBox.getSelectionModel().getSelectedItem();
        String searchText = searchTextField.getText();
        boolean sortByPriceBool = sortByPrice.isSelected();

        try {
            List<Product> products = ProductRepo.fetchFilteredAndSortedProducts(brand, category, year, searchText, sortByPriceBool);
            mainTableView.setItems(FXCollections.observableArrayList(products));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
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
        years.add("Year");
        years.addAll(fetchYearsFromDatabase());
        return years;
    }

    private void fetchProductsFromDatabase() {
        try {
            List<Product> productList = ProductRepo.getAllProducts();
            products.setAll(productList);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void displayProductDetails(int productId) {
        try {
            Product product = ProductRepo.getProductById(productId);
            if (product != null) {
                titleLabel.setText(product.getTitle());
                descriptionLabel.setText(product.getDescription());
                brandLabel.setText(product.getBrand());
                priceLabel.setText(product.getPrice().toString());

                if (product.getImage() != null) {
                    mainImageView.setImage(ImageConverter.byteArrayToImage(product.getImage()));
                } else {
                    mainImageView.setImage(null);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addToCart() {
        try {
            Product selectedProduct = mainTableView.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                System.out.println("No product selected.");
                return;
            }

            if (currentOrder == null) {
                // Create a new order with status IN_PROGRESS
                currentOrder = new Order();
                currentOrder.setStatus(OrderStatus.IN_PROGRESS);
                OrderRepo.createOrder(currentOrder);
            }

            // Add order item to the current order
            OrderItemRepo.addOrderItem(currentOrder, selectedProduct, quantity);
            System.out.println("Product added to cart.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
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
