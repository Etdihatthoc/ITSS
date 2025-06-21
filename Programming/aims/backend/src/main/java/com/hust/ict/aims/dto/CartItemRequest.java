package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemRequest {
    // Getters and setters
    private Long productId;
    private int quantity;

}