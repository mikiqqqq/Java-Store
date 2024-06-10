package org.store;

import database.repository.OrderRepo;
import database.repository.ProductRepo;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.store.enumeration.OrderStatus;
import org.store.model.Order;
import org.store.model.OrderItem;
import org.store.model.Product;
import org.store.model.User;
import org.store.utils.ApiService;
import org.store.utils.ImageConverter;
import org.store.utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

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
    private ToggleGroup sortToggleGroup;

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

    private int quantity = 1;
    private Order currentOrder;

    private Product selectedProduct;

    private final ApiService productApiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20); // Adjust the pool size as needed

    public MainController() {
        this.productApiService = new ApiService("http://localhost:8080/api/order-item/");
    }

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
        mainTableView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                displayProductDetails(newSelection.getId());
                quantity = 1;
                quantityLabel.setText("1");
                selectedProduct = newSelection;
            }
        });

        // Add listener to search field
        searchTextField.textProperty().addListener((_, _, _) -> filterAndSortProducts());

        // Add listener to sort toggle group for sorting
        sortToggleGroup.selectedToggleProperty().addListener((_, _, _) -> filterAndSortProducts());

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
        brandChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> filterAndSortProducts());
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> filterAndSortProducts());
        yearChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> filterAndSortProducts());
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
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to add to cart");
            System.out.println("No product selected.");
            return;
        }

        executorService.submit(() -> {
            try {
                if (currentOrder == null) {
                    User user = UserSession.getInstance().getUser();
                    currentOrder = OrderRepo.getOrderInProgressByEmail(user.getEmail());
                    if(currentOrder == null) {
                        currentOrder = new Order();
                        currentOrder.setStatus(OrderStatus.IN_PROGRESS);
                        currentOrder.setEmail(user.getEmail());
                        currentOrder.setUserId(user.getId());
                        OrderRepo.createOrder(currentOrder);
                    }
                }
                OrderItem orderItem = new OrderItem(quantity, currentOrder.getId(), selectedProduct.getId());
                // Add order item to the current order
                CloseableHttpResponse response = productApiService.sendRequest("add", orderItem, "POST");
                handleResponse(response, "Order item added successfully.");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to add order item: " + e.getMessage());
            }
        });
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
