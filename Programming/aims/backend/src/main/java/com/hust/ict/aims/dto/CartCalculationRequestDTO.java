package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CartCalculationRequestDTO {
    // Getters and setters
    private List<CartItemDTO> items;
    private boolean isRushDelivery;
    private String province;
}
