package org.store.model;

import java.math.BigDecimal;

public class OrderDisplay {
    private String date;
    private String status;
    private BigDecimal price;
    private String products;
    private int orderId;

    public OrderDisplay() {}

    public OrderDisplay(String date, String status, BigDecimal price, String products, int orderId) {
        this.date = date;
        this.status = status;
        this.price = price;
        this.products = products;
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }
}
