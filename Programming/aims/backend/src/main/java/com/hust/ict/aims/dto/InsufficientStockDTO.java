package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InsufficientStockDTO {
    public Long productId;
    public String productName;
    public int requestedQuantity;
    public int availableQuantity;

    public InsufficientStockDTO(Long productId, String productName, int requested, int available) {
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requested;
        this.availableQuantity = available;
    }

    // Getters and setters if needed
}