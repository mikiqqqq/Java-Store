package database.repository;

import org.store.model.Product;
import database.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {

    public static List<Product> getAllProducts() throws SQLException, IOException {
        List<Product> products = new ArrayList<>();
        Connection connection = Database.getConnect();

        String query = "SELECT P.ID, P.TITLE, P.DESCRIPTION, P.PRICE, P.QUANTITY, P.IMAGE, B.TITLE AS BRAND_TITLE, C.TITLE AS CATEGORY_TITLE, PY.PRODUCT_YEAR " +
                "FROM PRODUCT P " +
                "JOIN BRAND B ON P.BRAND_ID = B.ID " +
                "JOIN CATEGORY C ON P.CATEGORY_ID = C.ID " +
                "JOIN PRODUCT_YEAR PY ON P.PRODUCT_YEAR_ID = PY.ID";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Product product = new Product();
            product.setId(resultSet.getInt("ID"));
            product.setTitle(resultSet.getString("TITLE"));
            product.setDescription(resultSet.getString("DESCRIPTION"));
            product.setPrice(resultSet.getBigDecimal("PRICE"));
            product.setQuantity(resultSet.getInt("QUANTITY"));
            product.setImage(resultSet.getBytes("IMAGE"));
            product.setBrand(resultSet.getString("BRAND_TITLE"));
            product.setCategory(resultSet.getString("CATEGORY_TITLE"));
            product.setProductYear(resultSet.getInt("PRODUCT_YEAR"));
            products.add(product);
        }

        connection.close();
        return products;
    }

    public static Product getProductById(int productId) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        Product product = null;

        String query = "SELECT P.ID, P.TITLE, P.DESCRIPTION, P.PRICE, P.QUANTITY, P.IMAGE, B.TITLE AS BRAND_TITLE, C.TITLE AS CATEGORY_TITLE, PY.PRODUCT_YEAR " +
                "FROM PRODUCT P " +
                "JOIN BRAND B ON P.BRAND_ID = B.ID " +
                "JOIN CATEGORY C ON P.CATEGORY_ID = C.ID " +
                "JOIN PRODUCT_YEAR PY ON P.PRODUCT_YEAR_ID = PY.ID " +
                "WHERE P.ID = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, productId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            product = new Product();
            product.setId(resultSet.getInt("ID"));
            product.setTitle(resultSet.getString("TITLE"));
            product.setDescription(resultSet.getString("DESCRIPTION"));
            product.setPrice(resultSet.getBigDecimal("PRICE"));
            product.setQuantity(resultSet.getInt("QUANTITY"));
            product.setImage(resultSet.getBytes("IMAGE"));
            product.setBrand(resultSet.getString("BRAND_TITLE"));
            product.setCategory(resultSet.getString("CATEGORY_TITLE"));
            product.setProductYear(resultSet.getInt("PRODUCT_YEAR"));
        }

        connection.close();
        return product;
    }

    public static void addProduct(Product product) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "INSERT INTO PRODUCT (TITLE, DESCRIPTION, PRICE, QUANTITY, IMAGE, BRAND_ID, CATEGORY_ID, PRODUCT_YEAR_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, product.getTitle());
        statement.setString(2, product.getDescription());
        statement.setBigDecimal(3, product.getPrice());
        statement.setInt(4, product.getQuantity());
        statement.setBytes(5, product.getImage());
        statement.setInt(6, getBrandId(product.getBrand()));
        statement.setInt(7, getCategoryId(product.getCategory()));
        statement.setInt(8, getProductYearId(product.getProductYear()));

        statement.executeUpdate();
        connection.close();
    }

    private static int getBrandId(String brandTitle) throws SQLException, IOException {
        return getIdFromTable("BRAND", brandTitle);
    }

    private static int getCategoryId(String categoryTitle) throws SQLException, IOException {
        return getIdFromTable("CATEGORY", categoryTitle);
    }

    private static int getProductYearId(int productYear) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        int id = -1;

        String query = "SELECT ID FROM PRODUCT_YEAR WHERE PRODUCT_YEAR = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, productYear);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }

        connection.close();
        return id;
    }

    private static int getIdFromTable(String tableName, String title) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        int id = -1;

        String query = "SELECT ID FROM " + tableName + " WHERE TITLE = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, title);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }

        connection.close();
        return id;
    }
}