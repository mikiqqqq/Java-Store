package database.repository;

import database.Database;
import org.store.model.OrderItem;
import org.store.model.Order;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo {

    public static Order createOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String query = "INSERT INTO \"ORDER\" (DATE, STATUS) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(2, order.getStatus().name());

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating order failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }
        }

        connection.close();
        return order;
    }

    public static void updateOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String query = "UPDATE \"ORDER\" SET PAYMENT_TYPE_ID = ?, CARD_NUMBER = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ?, MESSAGE = ?, STATUS = ? WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, order.getPaymentTypeId());
        statement.setString(2, order.getCardNumber());
        statement.setString(3, order.getEmail());
        statement.setString(4, order.getPhoneNumber());
        statement.setString(5, order.getAddress());
        statement.setString(6, order.getMessage());
        statement.setString(7, order.getStatus().name());
        statement.setInt(8, order.getId());

        statement.executeUpdate();
        connection.close();
    }

    public static void addOrderProduct(OrderItem orderItem) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String query = "INSERT INTO ORDER_PRODUCTS (ORDER_ID, PRODUCT_ID) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, orderItem.getOrderId());
        statement.setInt(2, orderItem.getProductId());

        statement.executeUpdate();
        connection.close();
    }

    public static List<Order> getOrdersByStatus(Order.Status status) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        String query = "SELECT * FROM \"ORDER\" WHERE STATUS = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, status.name());

        ResultSet resultSet = statement.executeQuery();
        List<Order> orders = new ArrayList<>();

        while (resultSet.next()) {
            Order order = new Order();
            order.setId(resultSet.getInt("ID"));
            order.setDate(resultSet.getTimestamp("DATE").toLocalDateTime());
            order.setPaymentTypeId(resultSet.getInt("PAYMENT_TYPE_ID"));
            order.setCardNumber(resultSet.getString("CARD_NUMBER"));
            order.setEmail(resultSet.getString("EMAIL"));
            order.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));
            order.setAddress(resultSet.getString("ADDRESS"));
            order.setMessage(resultSet.getString("MESSAGE"));
            order.setStatus(Order.Status.valueOf(resultSet.getString("STATUS")));
            orders.add(order);
        }

        connection.close();
        return orders;
    }
}