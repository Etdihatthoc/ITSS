// src/main/java/com/hust/ict/aims/dto/CartRequest.java
package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class CartRequest {
    // Getters and setters
    @Getter
    private long cartId;
    private double totalProductPriceBeforeVAT;
    @Getter
    private List<CartItemRequest> items;

    public float getTotalProductPriceBeforeVAT() {
        return (float) totalProductPriceBeforeVAT;
    }

}