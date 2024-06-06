package org.store.model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private byte[] image;
    private String title;
    private String description;
    private BigDecimal price;
    private int quantity;
    private int brandId;
    private int categoryId;
    private int productYearId;
    private String brand;
    private String category;
    private String productYear;

    // Constructors
    public Product() {}

    public Product(int id, byte[] image, String title, String description, BigDecimal price, int quantity, int brandId, int categoryId, int productYearId, String brand, String category, String productYear) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.productYearId = productYearId;
        this.brand = brand;
        this.category = category;
        this.productYear = productYear;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getProductYearId() {
        return productYearId;
    }

    public void setProductYearId(int productYearId) {
        this.productYearId = productYearId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductYear() {
        return productYear;
    }

    public void setProductYear(String productYear) {
        this.productYear = productYear;
    }
}