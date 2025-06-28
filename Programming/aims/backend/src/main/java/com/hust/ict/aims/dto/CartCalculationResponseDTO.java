package com.hust.ict.aims.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class CartCalculationResponseDTO {
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double rushDeliveryFee;
    private double total;
    private List<CartItemDetailDTO> items;
    private boolean allItemsAvailable;
    private List<InsufficientStockDTO> outOfStockItems;
}