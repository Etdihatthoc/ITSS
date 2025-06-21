package com.hust.ict.aims.dto;

public class OutOfStockProduct {
    private Long productId;
    private int requested;
    private int available;
    private String title;
    private String message;

    // Constructor
    public OutOfStockProduct(Long productId, int requested, int available, String title, String message) {
        this.productId = productId;
        this.requested = requested;
        this.available = available;
        this.title = title;
        this.message = message;
    }

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getRequested() {
        return requested;
    }

    public void setRequested(int requested) {
        this.requested = requested;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}