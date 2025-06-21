package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceRequestDTO {
    private int cartId;
    private float totalProductPriceAfterVAT;
    private float totalAmount;
    private float deliveryFee;

}