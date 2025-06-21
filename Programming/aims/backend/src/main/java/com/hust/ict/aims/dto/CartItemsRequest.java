package com.hust.ict.aims.dto;

import java.util.List;

public class CartItemsRequest {
    private List<CartItemRequest> items;

    // Getters and setters
    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }
}
