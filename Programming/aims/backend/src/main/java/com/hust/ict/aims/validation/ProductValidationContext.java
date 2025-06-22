package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductValidationContext {
    
    private final Map<Class<? extends Product>, ProductValidationStrategy> strategies;
    private final BaseProductValidationStrategy baseProductValidationStrategy;
    
    @Autowired
    public ProductValidationContext(
            BookValidationStrategy bookStrategy,
            CDValidationStrategy cdStrategy,
            DVDValidationStrategy dvdStrategy,
            LPValidationStrategy lpStrategy,
            BaseProductValidationStrategy baseProductValidationStrategy) {
        
        this.baseProductValidationStrategy = baseProductValidationStrategy;
        this.strategies = new HashMap<>();
        strategies.put(Book.class, bookStrategy);
        strategies.put(CD.class, cdStrategy);
        strategies.put(DVD.class, dvdStrategy);
        strategies.put(LP.class, lpStrategy);
        strategies.put(Product.class, baseProductValidationStrategy); // Add base class
    }
    
    public ValidationResult validate(Product product) {
        // Get the exact class of the product
        Class<? extends Product> productClass = product.getClass();
        
        ProductValidationStrategy strategy = strategies.get(productClass);
        
        // If no specific strategy found, use base product validation
        if (strategy == null) {
            strategy = baseProductValidationStrategy;
        }
        
        return strategy.validate(product);
    }
}