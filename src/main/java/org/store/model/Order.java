package org.store.model;

import java.sql.Timestamp;

import org.store.enumeration.OrderStatus;

public class Order {
    private int id;
    private Timestamp date;
    private String cardNumber;
    private String email;
    private String phoneNumber;
    private String address;
    private String message;
    private OrderStatus status;
    private int userId;

    // Constructors
    public Order() {
        this.status = OrderStatus.IN_PROGRESS;
    }

    public Order(int id, Timestamp date, String cardNumber, String email, String phoneNumber, String address, OrderStatus status) {
        this.id = id;
        this.date = date;
        this.cardNumber = cardNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp  getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
