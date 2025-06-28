// src/main/java/com/hust/ict/aims/dto/CartRequest.java
package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class CartRequestDTO {
    // Getters and setters
    @Getter
    private long cartId;
    private double totalProductPriceBeforeVAT;
    @Getter
    private List<CartItemDTO> items;

    public float getTotalProductPriceBeforeVAT() {
        return (float) totalProductPriceBeforeVAT;
    }

}