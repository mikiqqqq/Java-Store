package org.store.model;

import java.math.BigDecimal;

public class OrderItemDisplay {
    private final int id;
    private final int orderId;
    private final int productId;
    private final int quantity;
    private final String productName;
    private final BigDecimal totalPrice;

    // Constructor
    public OrderItemDisplay(OrderItem orderItem, String productName, BigDecimal totalPrice) {
        this.id = orderItem.getId();
        this.orderId = orderItem.getOrderId();
        this.productId = orderItem.getItemId();
        this.quantity = orderItem.getQuantity();
        this.productName = productName;
        this.totalPrice = totalPrice;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
