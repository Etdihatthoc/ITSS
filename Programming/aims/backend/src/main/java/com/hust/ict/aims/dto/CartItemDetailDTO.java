package com.hust.ict.aims.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class CartItemDetailDTO {
    private Long productId;
    private String title;
    private double price;
    private int quantity;
    private double subtotal;
    private String imageURL;
    private String category;
    private float weight;
}