package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.validation.ProductValidationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductValidationService {
    
    @Autowired
    private List<ProductValidationStrategy> strategies;
    
    public void validateProductInformation(Product product) {
        ProductValidationStrategy strategy = strategies.stream()
            .filter(s -> s.supports(product))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "No validation strategy found for product type"));
        
        strategy.validate(product);
    }
}