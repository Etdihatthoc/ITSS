package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseProductValidationStrategy implements ProductValidationStrategy {
    
    protected void validateCommonFields(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product title must not be empty");
        }
        
        if (product.getImageURL() == null || product.getImageURL().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product image URL must not be empty");
        }
        
        if (product.getProductDimensions() == null || product.getProductDimensions().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product dimensions must not be empty");
        }
        
        if (product.getWarehouseEntryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Warehouse entry date must not be empty");
        }
        
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product category must not be empty");
        }
        
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product barcode must not be empty");
        }
        
        if (product.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product value must be greater than zero");
        }
        
        if (product.getCurrentPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price must be greater than zero");
        }
        
        if (product.getWeight() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product weight must be greater than zero");
        }
        
        if (product.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product quantity cannot be negative");
        }
    }
}