package org.store.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    public enum Status {
        IN_PROGRESS,
        COMPLETED
    }

    private int id;
    private LocalDateTime date;
    private int paymentTypeId;
    private String cardNumber;
    private String email;
    private String phoneNumber;
    private String address;
    private String message;
    private Status status;
    private List<OrderItem> orderItems; // Association with OrderProduct class

    // Constructors
    public Order() {
        this.status = Status.IN_PROGRESS; // Default status
    }

    public Order(int id, LocalDateTime date, int paymentTypeId, String cardNumber, String email, String phoneNumber, String address, String message, Status status, List<OrderItem> orderItems) {
        this.id = id;
        this.date = date;
        this.paymentTypeId = paymentTypeId;
        this.cardNumber = cardNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.message = message;
        this.status = status;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<OrderItem> getOrderProducts() {
        return orderItems;
    }

    public void setOrderProducts(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
