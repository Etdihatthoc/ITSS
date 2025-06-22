package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component("baseProductValidation")
public class BaseProductValidationStrategy implements ProductValidationStrategy {
    
    @Override
    public ValidationResult validate(Product product) {
        ValidationResult result = new ValidationResult();
        
        // Only validate basic product fields for base Product class
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            result.addError("Product title is required");
        }
        
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            result.addError("Product category is required");
        }
        
        if (product.getValue() <= 0) {
            result.addError("Product value must be greater than zero");
        }
        
        if (product.getCurrentPrice() <= 0) {
            result.addError("Product price must be greater than zero");
        }
        
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            result.addError("Product barcode is required");
        }
        
        if (product.getProductDimensions() == null || product.getProductDimensions().trim().isEmpty()) {
            result.addError("Product dimensions are required");
        }
        
        if (product.getWeight() <= 0) {
            result.addError("Product weight must be greater than zero");
        }
        
        if (product.getQuantity() < 0) {
            result.addError("Product quantity cannot be negative");
        }
        
        if (product.getWarehouseEntryDate() == null) {
            result.addError("Warehouse entry date is required");
        }
        
        if (product.getImageURL() == null || product.getImageURL().trim().isEmpty()) {
            result.addError("Product image URL is required");
        }
        
        return result;
    }
}