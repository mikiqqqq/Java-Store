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
        return mapResultSetToProduct(connection, products, statement);
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
            product.setProductYear(resultSet.getString("PRODUCT_YEAR"));
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

    public static List<Product> searchProductsByTitle(String target) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<Product> products = new ArrayList<>();

        String query = "SELECT P.ID, P.IMAGE, P.TITLE, P.DESCRIPTION, P.PRICE, P.QUANTITY, B.TITLE AS BRAND_TITLE, C.TITLE AS CATEGORY_TITLE, PY.PRODUCT_YEAR " +
                "FROM PRODUCT P " +
                "JOIN BRAND B ON P.BRAND_ID = B.ID " +
                "JOIN CATEGORY C ON P.CATEGORY_ID = C.ID " +
                "JOIN PRODUCT_YEAR PY ON P.PRODUCT_YEAR_ID = PY.ID " +
                "WHERE LOWER(P.TITLE) LIKE LOWER(?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + target + "%");

        return mapResultSetToProduct(connection, products, statement);
    }

    public static List<Product> fetchFilteredAndSortedProducts(String brand, String category, String year, String searchText, boolean sortByPrice) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<Product> products = new ArrayList<>();

        StringBuilder queryBuilder = getStringBuilder(brand, category, year, searchText, sortByPrice);

        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

        int paramIndex = 1;
        if (brand != null) {
            statement.setString(paramIndex++, brand);
        }
        if (category != null) {
            statement.setString(paramIndex++, category);
        }
        if (year != null) {
            statement.setString(paramIndex++, year);
        }
        if (searchText != null && !searchText.isEmpty()) {
            statement.setString(paramIndex++, "%" + searchText + "%");
        }

        return mapResultSetToProduct(connection, products, statement);
    }

    private static StringBuilder getStringBuilder(String brand, String category, String year, String searchText, boolean sortByPrice) {
        StringBuilder queryBuilder = new StringBuilder("SELECT P.ID, P.IMAGE, P.TITLE, P.DESCRIPTION, P.PRICE, P.QUANTITY, B.TITLE AS BRAND_TITLE, C.TITLE AS CATEGORY_TITLE, PY.PRODUCT_YEAR " +
                "FROM PRODUCT P " +
                "JOIN BRAND B ON P.BRAND_ID = B.ID " +
                "JOIN CATEGORY C ON P.CATEGORY_ID = C.ID " +
                "JOIN PRODUCT_YEAR PY ON P.PRODUCT_YEAR_ID = PY.ID WHERE 1=1");

        if (brand != null) {
            queryBuilder.append(" AND B.TITLE = ?");
        }
        if (category != null) {
            queryBuilder.append(" AND C.TITLE = ?");
        }
        if (year != null) {
            queryBuilder.append(" AND PY.PRODUCT_YEAR = ?");
        }
        if (searchText != null && !searchText.isEmpty()) {
            queryBuilder.append(" AND LOWER(P.TITLE) LIKE LOWER(?)");
        }
        if (sortByPrice) {
            queryBuilder.append(" ORDER BY P.PRICE ASC");
        } else {
            queryBuilder.append(" ORDER BY UPPER(P.TITLE) ASC");
        }
        return queryBuilder;
    }

    private static List<Product> mapResultSetToProduct(Connection connection, List<Product> products, PreparedStatement statement) throws SQLException {
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
            product.setProductYear(resultSet.getString("PRODUCT_YEAR"));
            products.add(product);
        }

        connection.close();
        return products;
    }

    public static int getBrandId(String brandTitle) throws SQLException, IOException {
        return getIdFromTable("BRAND", brandTitle);
    }

    public static int getCategoryId(String categoryTitle) throws SQLException, IOException {
        return getIdFromTable("CATEGORY", categoryTitle);
    }

    public static int getProductYearId(String productYear) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        int id = -1;

        String query = "SELECT ID FROM PRODUCT_YEAR WHERE PRODUCT_YEAR = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, productYear);

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

    public static String getBrandById(int brandId) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String brand = "";

        String query = "SELECT TITLE FROM BRAND WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, brandId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            brand = resultSet.getString("TITLE");
        }

        connection.close();
        return brand;
    }

    public static String getCategoryById(int categoryId) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String category = "";

        String query = "SELECT TITLE FROM CATEGORY WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, categoryId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            category = resultSet.getString("TITLE");
        }

        connection.close();
        return category;
    }

    public static int getYearById(int yearId) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        int year = 0;

        String query = "SELECT PRODUCT_YEAR FROM PRODUCT_YEAR WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, yearId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            year = resultSet.getInt("PRODUCT_YEAR");
        }

        connection.close();
        return year;
    }

    public static List<String> getAllBrands() throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<String> brands = new ArrayList<>();

        String query = "SELECT TITLE FROM BRAND";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            brands.add(resultSet.getString("TITLE"));
        }

        connection.close();
        return brands;
    }

    public static List<String> getAllCategories() throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<String> categories = new ArrayList<>();

        String query = "SELECT TITLE FROM CATEGORY";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            categories.add(resultSet.getString("TITLE"));
        }

        connection.close();
        return categories;
    }

    public static List<String> getAllYears() throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<String> years = new ArrayList<>();

        String query = "SELECT PRODUCT_YEAR FROM PRODUCT_YEAR";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            years.add(resultSet.getString("PRODUCT_YEAR"));
        }

        connection.close();
        return years;
    }
}
