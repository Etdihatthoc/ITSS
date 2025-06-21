package com.hust.ict.aims.dto;

public class CartItemRequestDTO {
    public Long productId;
    public int quantity;

    public void setProductId(long l) {
        this.productId = l;
    }

    public void setQuantity(int i) {
        this.quantity = i;
    }
}