package database.repository;

import database.Database;
import org.store.enumeration.OrderStatus;
import org.store.model.OrderItem;
import org.store.model.Order;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo {

    public static void createOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        // Adjust the SQL query to insert both STATUS and EMAIL
        String query = "INSERT INTO \"ORDER\" (STATUS, EMAIL) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, new String[]{"ID"});
        statement.setString(1, order.getStatus().name());
        statement.setString(2, order.getEmail());

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
        String query = "UPDATE \"ORDER\" SET CARD_NUMBER = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ?, MESSAGE = ?, STATUS = ? WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, order.getCardNumber());
        statement.setString(2, order.getEmail());
        statement.setString(3, order.getPhoneNumber());
        statement.setString(4, order.getAddress());
        statement.setString(5, order.getMessage());
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
}