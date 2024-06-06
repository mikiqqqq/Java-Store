package database.repository;

import database.Database;
import org.store.model.Order;
import org.store.model.OrderItem;
import org.store.model.Product;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepo {

    public static void addOrderItem(OrderItem orderItem) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "INSERT INTO ORDER_ITEM (QUANTITY, ORDER_ID, PRODUCT_ID) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, orderItem.getQuantity());
        statement.setInt(2, orderItem.getOrderId());
        statement.setInt(3, orderItem.getProductId());

        statement.executeUpdate();
        connection.close();
    }

    public static void addOrderItem(Order order, Product product, int quantity) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        // Check if the order exists
        if (order.getId() == 0) {
            // Create a new order
            String insertOrderQuery = "INSERT INTO \"ORDER\" (DATE, PAYMENT_TYPE_ID, STATUS) VALUES (CURRENT_TIMESTAMP, ?, ?)";
            PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStatement.setInt(1, 1); // Assume PAYMENT_TYPE_ID is 1 for now
            orderStatement.setString(2, "IN_PROGRESS");
            orderStatement.executeUpdate();

            // Get the generated order ID
            ResultSet generatedKeys = orderStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getInt(1));
            }
        }

        // Add the order item
        String insertOrderItemQuery = "INSERT INTO ORDER_ITEM (QUANTITY, ORDER_ID, PRODUCT_ID) VALUES (?, ?, ?)";
        PreparedStatement orderItemStatement = connection.prepareStatement(insertOrderItemQuery);
        orderItemStatement.setInt(1, quantity);
        orderItemStatement.setInt(2, order.getId());
        orderItemStatement.setInt(3, product.getId());
        orderItemStatement.executeUpdate();

        connection.close();
    }

    public static OrderItem getOrderItemById(int id) throws SQLException, IOException {
        Connection connection = Database.getConnect();
        OrderItem orderItem = null;

        String query = "SELECT * FROM ORDER_ITEM WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            orderItem = new OrderItem();
            orderItem.setId(resultSet.getInt("ID"));
            orderItem.setQuantity(resultSet.getInt("QUANTITY"));
            orderItem.setOrderId(resultSet.getInt("ORDER_ID"));
            orderItem.setProductId(resultSet.getInt("PRODUCT_ID"));
        }

        connection.close();
        return orderItem;
    }

    public static List<OrderItem> getAllOrderItems() throws SQLException, IOException {
        Connection connection = Database.getConnect();
        List<OrderItem> orderItems = new ArrayList<>();

        String query = "SELECT * FROM ORDER_ITEM";
        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(resultSet.getInt("ID"));
            orderItem.setQuantity(resultSet.getInt("QUANTITY"));
            orderItem.setOrderId(resultSet.getInt("ORDER_ID"));
            orderItem.setProductId(resultSet.getInt("PRODUCT_ID"));

            orderItems.add(orderItem);
        }

        connection.close();
        return orderItems;
    }
}
