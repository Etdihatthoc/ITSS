package com.hust.ict.aims.dto;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private int quantity;
    private String operation;  // "increase" or "decrease"
}