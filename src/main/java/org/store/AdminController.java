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
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.store.model.Product;
import org.store.model.Settings;
import org.store.utils.ImageBlobConverter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
    private byte[] imageBytes;

    private ResourceBundle bundle;

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
                setFormState("edit");
                populateProductForm(newSelection);
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
    private void handleEdit(ActionEvent event) {
        Product selectedProduct = mainTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            setFormState("edit");
            populateProductForm(selectedProduct);
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
    }

    private void populateProductForm(Product product) {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);

        titleTextField.setText(product.getTitle());
        descriptionTextArea.setText(product.getDescription());
        brandChoiceBox.setValue(product.getBrand());
        yearChoiceBox.setValue(product.getProductYear());
        categoryChoiceBox.setValue(bundle.getString("menu." + product.getCategory().toLowerCase()));
        priceTextField.setText(String.valueOf(product.getPrice()));
        productImageView.setImage(ImageBlobConverter.byteArrayToImage(product.getImage()));
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Read the file into a byte array
                imageBytes = ImageBlobConverter.imageToByteArray(file);

                // Display the image in the ImageView
                Image image = new Image(file.toURI().toString());
                productImageView.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addProduct() throws SQLException, IOException {
        // Collect data and send it to the API
        Product product = collectProductData();
        addProductToDatabase(product);
    }

    @FXML
    private void editProduct(Product product) throws SQLException, IOException {
        // Update the product object with new data
        Product updatedProduct = collectProductData();
        updatedProduct.setId(product.getId());
        updateProductInDatabase(updatedProduct);
    }

    private void addProductToDatabase(Product product) {
        String url = "http://localhost:8080/api/product/add";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(product);

            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            httpPut.setEntity(entity);
            httpPut.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                if (response.getCode() != 200) {
                    // Handle non-OK response
                    System.err.println("Failed to add product: " + response.getReasonPhrase());
                } else {
                    updateTableData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProductInDatabase(Product product) {
        String url = "http://localhost:8080/api/product/update";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(product);

            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            httpPut.setEntity(entity);
            httpPut.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                if (response.getCode() != 200) {
                    // Handle non-OK response
                    System.err.println("Failed to update product: " + response.getReasonPhrase());
                } else {
                    updateTableData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Product collectProductData() throws SQLException, IOException {
        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        Integer brand = ProductRepo.getBrandId(brandChoiceBox.getValue());
        Integer year = ProductRepo.getProductYearId(yearChoiceBox.getValue());
        Integer category = ProductRepo.getCategoryId(categoryChoiceBox.getValue());
        double price = Double.parseDouble(priceTextField.getText());

        return new Product(title, description, brand, year, category, price, imageBytes);
    }
}
