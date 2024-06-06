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

    public static void createOrder(Order order) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "INSERT INTO \"ORDER\" (STATUS) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(query, new String[]{"ID"});
        statement.setString(1, order.getStatus().name());

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
}