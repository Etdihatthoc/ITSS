package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Component
public class UpdateProductOperation extends ProductOperationTemplate {
    
    private Long productId;
    
    public UpdateProductOperation setProductId(Long productId) {
        this.productId = productId;
        return this;
    }
    
    @Override
    protected void validateBusinessRules(Product product, String operationType) {
        super.validateBusinessRules(product, operationType);
        
        // Check if product exists
        Product existingProduct = productService.findById(productId);
        if (existingProduct == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product with ID " + productId + " not found"
            );
        }
        
        // Check price update frequency if price changed
        if (existingProduct.getCurrentPrice() != product.getCurrentPrice()) {
            validatePriceRange(product.getValue(), product.getCurrentPrice());
            validatePriceUpdateFrequency(productId);
        }
    }
    
    @Override
    protected Product performOperation(Product product) {
        product.setId(productId);
        return productService.save(product);
    }
    
    private void validatePriceRange(float productValue, float newPrice) {
        float minPrice = productValue * 0.3f;
        float maxPrice = productValue * 1.5f;

        if (newPrice < minPrice || newPrice > maxPrice) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New price must be between 30% and 150% of the product value"
            );
        }
    }

    private void validatePriceUpdateFrequency(Long productId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        long updateCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                productId, "UPDATE_PRODUCT", startOfDay, endOfDay);

        if (updateCount >= 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot update product price more than 2 times per day"
            );
        }
    }
}