// src/main/java/com/hust/ict/aims/dto/InvoiceRequest.java
package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.Cart;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceRequest {
    // Getters and setters
    private CartRequest cart;
    private float totalProductPriceBeforeVAT;
    private float totalProductPriceAfterVAT;
    private float deliveryFee;
    private float totalAmount;

}