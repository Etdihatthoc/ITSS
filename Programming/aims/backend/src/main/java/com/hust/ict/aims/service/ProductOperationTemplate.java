package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Operation;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.validation.ProductValidationContext;
import com.hust.ict.aims.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Component
public abstract class ProductOperationTemplate {
    
    @Autowired
    protected ProductValidationContext validationContext;
    
    @Autowired
    protected BusinessRulesService businessRulesService;
    
    @Autowired
    protected OperationService operationService;
    
    @Autowired
    protected ProductService productService;
    
    // Template method
    public final Product executeOperation(Product product, String operationType) {
        try {
            // Step 1: Acquire operation lock (if needed)
            businessRulesService.acquireOperationLock(operationType);
            
            // Step 2: Validate business rules
            validateBusinessRules(product, operationType);
            
            // Step 3: Validate product information
            validateProduct(product);
            
            // Step 4: Perform the actual operation
            Product result = performOperation(product);
            
            // Step 5: Log the operation
            logOperation(result, operationType);
            
            return result;
            
        } finally {
            // Step 6: Release operation lock
            businessRulesService.releaseOperationLock(operationType);
        }
    }
    
    // Abstract method to be implemented by concrete classes
    protected abstract Product performOperation(Product product);
    
    // Template methods with default implementations
    protected void validateBusinessRules(Product product, String operationType) {
        businessRulesService.validateDailyOperationLimits(operationType, LocalDateTime.now());
    }
    
    protected void validateProduct(Product product) {
        // Basic validation (existing logic)
        validateBasicProductInformation(product);
        
        // Media-specific validation using Strategy pattern
        ValidationResult result = validationContext.validate(product);
        if (!result.isValid()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product validation failed: " + String.join(", ", result.getErrors())
            );
        }
    }
    
    protected void logOperation(Product product, String operationType) {
        Operation op = new Operation();
        op.setProduct(product);
        op.setOperationType(operationType);
        op.setTimestamp(LocalDateTime.now());
        operationService.save(op);
    }
    
    // Existing basic validation logic
    private void validateBasicProductInformation(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product title must not be empty"
            );
        }
        
        if (product.getImageURL() == null || product.getImageURL().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product image URL must not be empty"
            );
        }
        
        if (product.getProductDimensions() == null || product.getProductDimensions().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product dimensions must not be empty"
            );
        }
        
        if (product.getWarehouseEntryDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warehouse entry date must not be empty"
            );
        }
        
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product category must not be empty"
            );
        }
        
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product barcode must not be empty"
            );
        }
        
        if (product.getValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product value must be greater than zero"
            );
        }
        
        if (product.getCurrentPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product price must be greater than zero"
            );
        }
        
        if (product.getWeight() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product weight must be greater than zero"
            );
        }
        
        if (product.getQuantity() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product quantity cannot be negative"
            );
        }
    }
}