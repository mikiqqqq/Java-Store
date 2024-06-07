package org.store;

import database.repository.ProductRepo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.store.model.Product;

import java.io.File;
import java.io.IOException;
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

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        // Initialize the table columns
        columnId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        columnTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        columnBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        columnCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        columnYear.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProductYear())));
        columnPrice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice().toString()));

        updateTableData();

        // Initialize ResourceBundle for localization
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
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

    @FXML
    private void handleAddNew(ActionEvent event) {
        showProductForm(true);
        clearProductForm();
        actionButton.setText(bundle.getString("add_product"));
        actionButton.setOnAction(e -> addProduct());
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Product selectedProduct = mainTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showProductForm(true);
            populateProductForm(selectedProduct);
            actionButton.setText(bundle.getString("save"));
            actionButton.setOnAction(e -> editProduct(selectedProduct));
        } else {
            // Show a warning if no product is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("warning"));
            alert.setHeaderText(bundle.getString("no_product_selected"));
            alert.setContentText(bundle.getString("select_product_to_edit"));
            alert.showAndWait();
        }
    }

    private void showProductForm(boolean show) {
        productForm.setVisible(show);
        productForm.setManaged(show);
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
        titleTextField.setText(product.getTitle());
        descriptionTextArea.setText(product.getDescription());
        brandChoiceBox.setValue(product.getBrand());
        categoryChoiceBox.setValue(product.getCategory());
        priceTextField.setText(String.valueOf(product.getPrice()));
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
        }
    }

    private void addProduct() {
        // Collect data and send it to the API
        Product product = collectProductData();
        // Call API to add the product
    }

    private void editProduct(Product product) {
        // Update the product object with new data
        Product updatedProduct = collectProductData();
        updatedProduct.setId(product.getId()); // Keep the original ID
        // Call API to update the product
    }

    private Product collectProductData() {
        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        String brand = brandChoiceBox.getValue();
        String year = yearChoiceBox.getValue();
        String category = categoryChoiceBox.getValue();
        double price = Double.parseDouble(priceTextField.getText());
        String imageUrl = productImageView.getImage().getUrl();

        return new Product();
    }
}
