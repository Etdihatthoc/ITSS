package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.ProductDTO;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.fromEntity(product);
    }
}

