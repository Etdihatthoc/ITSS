package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InventoryCheckResponse {
    // Getters and setters
    private boolean allAvailable;
    private List<OutOfStockProduct> outOfStockProducts;

    // Constructor
    public InventoryCheckResponse(boolean allAvailable, List<OutOfStockProduct> outOfStockProducts) {
        this.allAvailable = allAvailable;
        this.outOfStockProducts = outOfStockProducts;
    }

}