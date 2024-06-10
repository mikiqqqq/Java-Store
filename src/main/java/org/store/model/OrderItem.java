package org.store.model;

public class OrderItem {
    private int id;
    private int quantity;
    private int orderId;
    private int itemId;

    // Constructors
    public OrderItem() {}

    public OrderItem(int quantity, int orderId, int itemId) {
        this.quantity = quantity;
        this.orderId = orderId;
        this.itemId = itemId;
    }

    public OrderItem(int id, int quantity, int orderId, int itemId) {
        this.id = id;
        this.quantity = quantity;
        this.orderId = orderId;
        this.itemId = itemId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}