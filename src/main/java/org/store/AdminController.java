package org.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import database.repository.ProductRepo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.store.model.Product;
import org.store.model.Settings;
import org.store.utils.ImageConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.store.services.ApiService;

public class AdminController {

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
    private ImageView productImageView;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ChoiceBox<String> brandChoiceBox;

    @FXML
    private ChoiceBox<String> yearChoiceBox;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private Button addNewProductButton;

    @FXML
    private Button editProductButton;

    @FXML
    private Button addProductButton;

    @FXML
    private Button saveProductButton;

    @FXML
    private Button removeProductButton;

    @FXML
    private Button selectImageButton;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private int selectedProductId;
    private ResourceBundle bundle;

    private ApiService productApiService;

    public AdminController() {
        this.productApiService = new ApiService("http://localhost:8080/api/product/");
    }

    @FXML
    public void initialize() throws SQLException, IOException {
        // Initialize the table columns
        columnId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        columnTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        columnBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        columnCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        columnYear.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProductYear())));
        columnPrice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice().toString()));

        updateTableData();

        // Initialize buttons visibility
        setFormState("new");

        populateChoiceBoxes();

        // Add listener to table row selection
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProductId = newSelection.getId();
                setFormState("edit");
                try {
                    populateProductForm(newSelection);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void populateChoiceBoxes() throws SQLException, IOException {
        // Apply language
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);

        brandChoiceBox.setItems(FXCollections.observableArrayList(ProductRepo.getAllBrands()));
        yearChoiceBox.setItems(FXCollections.observableArrayList(ProductRepo.getAllYears()));

        ObservableList<String> categoryItems = FXCollections.observableArrayList();
        for (String category : ProductRepo.getAllCategories()) {
            categoryItems.add(bundle.getString("menu." + category.toLowerCase()));
        }
        categoryChoiceBox.setItems(categoryItems);
    }

    private void updateTableData() {
        // Fetch products from the database
        fetchProductsFromDatabase();

        // Bind the products to the table
        mainTableView.setItems(products);
    }

    private void fetchProductsFromDatabase() {
        try {
            List<Product> productList = ProductRepo.getAllProducts();
            products.setAll(productList);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setFormState(String state) {
        if (state.equals("new")) {
            addProductButton.setVisible(true);
            addProductButton.setManaged(true);
            saveProductButton.setVisible(false);
            saveProductButton.setManaged(false);
            removeProductButton.setVisible(false);
            removeProductButton.setManaged(false);
        } else if (state.equals("edit")) {
            addProductButton.setVisible(false);
            addProductButton.setManaged(false);
            saveProductButton.setVisible(true);
            saveProductButton.setManaged(true);
            removeProductButton.setVisible(true);
            removeProductButton.setManaged(true);
        }
    }

    @FXML
    private void handleAddNew(ActionEvent event) {
        setFormState("new");
        clearProductForm();
    }

    @FXML
    private void handleEdit(ActionEvent event) throws IOException {
        Product selectedProduct = mainTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            setFormState("edit");
            populateProductForm(selectedProduct);
        } else {
            showAlert("Error", "You must select a product to edit.");
        }
    }

    private void clearProductForm() {
        productImageView.setImage(null);
        titleTextField.clear();
        descriptionTextArea.clear();
        brandChoiceBox.setValue(null);
        yearChoiceBox.setValue(null);
        categoryChoiceBox.setValue(null);
        priceTextField.clear();
        quantityTextField.clear();
    }

    private void populateProductForm(Product product) throws IOException {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
        titleTextField.setText(product.getTitle());
        descriptionTextArea.setText(product.getDescription());
        brandChoiceBox.setValue(product.getBrand());
        yearChoiceBox.setValue(product.getProductYear());
        categoryChoiceBox.setValue(bundle.getString("menu." + product.getCategory().toLowerCase()));
        priceTextField.setText(String.valueOf(product.getPrice()));
        if(product.getImage() != null) {
            productImageView.setImage(ImageConverter.byteArrayToImage(product.getImage()));
        }
        quantityTextField.setText(String.valueOf(product.getQuantity()));
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
        }
    }

    @FXML
    private void addProduct() throws SQLException, IOException {
        // Collect data and send it to the API
        Product product = collectProductData("new");

        try {
            boolean success = productApiService.sendRequest("add", product, "POST", Boolean.class);
            if (success) {
                updateTableData();
            } else {
                // Handle error
                System.err.println("Failed to add product.");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editProduct() throws SQLException, IOException {
        // Update the product object with new data
        Product updatedProduct = collectProductData("edit");
        try {
            boolean success = productApiService.sendRequest("update", updatedProduct, "PUT", Boolean.class);
            if (success) {
                updateTableData();
            } else {
                // Handle error
                System.err.println("Failed to update product.");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeProduct() throws SQLException, IOException {
        try {
            boolean success = productApiService.sendRequest(String.valueOf(selectedProductId), null, "DELETE", Boolean.class);
            if (success) {
                updateTableData();
            } else {
                // Handle error
                System.err.println("Failed to remove product.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Product collectProductData(String decider) throws SQLException, IOException {

        // Validate and collect title
        String title = titleTextField.getText();
        if (title == null || title.isEmpty()) {
            showAlert("Validation Error", "Title cannot be empty.");
            return null;
        }
        String description = descriptionTextArea.getText();
        // Validate brand selection
        String brandChoice = brandChoiceBox.getValue();
        if (brandChoice == null) {
            showAlert("Validation Error", "A brand must be selected.");
            return null;
        }
        int brand = ProductRepo.getBrandId(brandChoice);

        // Validate category selection
        String categoryChoice = categoryChoiceBox.getValue();
        if (categoryChoice == null) {
            showAlert("Validation Error", "A category must be selected.");
            return null;
        }
        int category = ProductRepo.getCategoryId(categoryChoice);

        // Validate year selection
        String yearChoice = yearChoiceBox.getValue();
        if (yearChoice == null) {
            showAlert("Validation Error", "A year must be selected.");
            return null;
        }
        int year = ProductRepo.getProductYearId(yearChoice);

        // Validate and collect price
        String priceText = priceTextField.getText();
        if (priceText == null || priceText.isEmpty()) {
            showAlert("Validation Error", "Price cannot be empty.");
            return null;
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid price format.");
            return null;
        }

        // Validate and collect quantity
        String quantityText = quantityTextField.getText();
        if (quantityText == null || quantityText.isEmpty()) {
            showAlert("Validation Error", "Quantity cannot be empty.");
            return null;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid quantity format.");
            return null;
        }

        // Get image bytes, null if no image is set
        Image image = productImageView.getImage();
        byte[] imageBytes = null;
        if (image != null) {
            imageBytes = ImageConverter.imageToByteArray(image, "png");
        }

        if (decider.equals("new")) {
            return new Product(title, description, brand, year, category, price, quantity, imageBytes);
        } else {
            int id = selectedProductId;
            return new Product(id, title, description, brand, year, category, price, quantity, imageBytes);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
