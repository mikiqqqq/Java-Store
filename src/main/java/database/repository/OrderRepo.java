package database.repository;

import database.Database;
import org.store.enumeration.OrderStatus;
import org.store.model.OrderItem;
import org.store.model.Order;
import org.store.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo {

    public static void createOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        // Adjust the SQL query to insert both STATUS and EMAIL
        String query = "INSERT INTO \"ORDER\" (USER_ID, STATUS, EMAIL) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, new String[]{"ID"});
        statement.setInt(1, order.getUserId());
        statement.setString(2, order.getStatus().name());
        statement.setString(3, order.getEmail());

        int affectedRows = statement.executeUpdate();
        if (affectedRows > 0) {
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getInt(1));
            }
        }

        connection.close();
    }

    public static void updateOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String query = "UPDATE \"ORDER\" SET DATE = ?, CARD_NUMBER = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ?, STATUS = ? WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setTimestamp(1, order.getDate());
        statement.setString(2, order.getCardNumber());
        statement.setString(3, order.getEmail());
        statement.setString(4, order.getPhoneNumber());
        statement.setString(5, order.getAddress());
        statement.setString(6, order.getStatus().name());
        statement.setInt(7, order.getId());

        statement.executeUpdate();
        connection.close();
    }

    public static Order getOrderInProgressByEmail(String email) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        Order order = null;

        String query = "SELECT * FROM \"ORDER\" WHERE STATUS = ? AND EMAIL = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        System.out.println(OrderStatus.IN_PROGRESS.name());
        System.out.println(email);
        statement.setString(1, OrderStatus.IN_PROGRESS.name());
        statement.setString(2, email);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            order = new Order();
            order.setId(resultSet.getInt("ID"));
            order.setEmail(resultSet.getString("EMAIL"));
            order.setStatus(OrderStatus.valueOf(resultSet.getString("STATUS")));
        }

        connection.close();
        return order;
    }

    public static void deleteOrderById(int orderId) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "DELETE FROM \"ORDER\" WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, orderId);

        statement.executeUpdate();

        connection.close();
    }

    public static BigDecimal getTotalPrice(int orderId) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "SELECT SUM(P.PRICE * OI.QUANTITY) AS TOTAL_PRICE " +
                "FROM ORDER_ITEM OI " +
                "JOIN PRODUCT P ON OI.PRODUCT_ID = P.ID " +
                "WHERE OI.ORDER_ID = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, orderId);

        ResultSet resultSet = statement.executeQuery();

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (resultSet.next()) {
            totalPrice = resultSet.getBigDecimal("TOTAL_PRICE");
        }

        connection.close();
        return totalPrice;
    }

    public static List<Product> getProductsByOrderId(int orderId) throws SQLException, IOException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.TITLE, p.PRICE, oi.QUANTITY " +
                "FROM PRODUCT p " +
                "JOIN ORDER_ITEM oi ON p.ID = oi.PRODUCT_ID " +
                "WHERE oi.ORDER_ID = ?";

        try (Connection connection = Database.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setTitle(resultSet.getString("TITLE"));
                product.setPrice(resultSet.getBigDecimal("PRICE"));
                product.setQuantity(resultSet.getInt("QUANTITY"));
                products.add(product);
            }
        }
        return products;
    }

    public static Order getOrderById(int orderId) throws SQLException, IOException {
        String query = "SELECT * FROM \"ORDER\" WHERE ID = ?";
        try (Connection connection = Database.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getInt("ID"));
                order.setDate(resultSet.getTimestamp("TIMESTAMP"));
                order.setCardNumber(resultSet.getString("CARD_NUMBER"));
                order.setEmail(resultSet.getString("EMAIL"));
                order.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));
                order.setAddress(resultSet.getString("ADDRESS"));
                order.setStatus(OrderStatus.valueOf(resultSet.getString("STATUS")));
                order.setUserId(resultSet.getInt("USER_ID"));
                return order;
            } else {
                return null; // Order not found
            }
        }
    }
}