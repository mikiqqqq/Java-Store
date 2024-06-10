package org.store.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.store.model.Order;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderJsonUtils {
    private static final String FILE_PATH = "src/main/resources/dat/";
    private static final String FILE_NAME = FILE_PATH + "orders.json";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Register the JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void addOrderToFile(Order order) throws IOException {
        List<Order> orders = new ArrayList<>(readOrdersFromFile());
        orders.add(order);
        saveOrdersToFile(orders);
    }

    // Save orders to JSON file
    public static void saveOrdersToFile(List<Order> orders) throws IOException {
        objectMapper.writeValue(new File(FILE_NAME), orders);
    }

    // Read orders from JSON file
    public static List<Order> readOrdersFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try {
                if (file.length() > 0) {
                    return objectMapper.readValue(file, new TypeReference<List<Order>>() {});
                } else {
                    System.out.println("The file is empty. Returning an empty list.");
                    return new ArrayList<>(); // Return an empty list if the file is empty
                }
            } catch (IOException e) {
                System.out.println("Failed to read orders from file: " + e.getMessage());
                return new ArrayList<>(); // Return an empty list if there is an exception
            }
        } else {
            System.out.println("The file does not exist. Returning an empty list.");
            return new ArrayList<>(); // Return an empty list if the file does not exist
        }
    }

    // Get orders by email
    public static List<Order> getOrdersByUserId(int userId) throws IOException {
        List<Order> orders = readOrdersFromFile();
        return orders.stream()
                .filter(order -> userId == order.getUserId())
                .collect(Collectors.toList());
    }

    public static Order getOrderById(int id) throws IOException {
        List<Order> orders = readOrdersFromFile();
        Optional<Order> orderOptional = orders.stream()
                .filter(order -> id == order.getId())
                .findFirst();
        return orderOptional.orElse(null);
    }
}
