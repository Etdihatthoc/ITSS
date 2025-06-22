package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component
public class AddProductOperation extends ProductOperationTemplate {
    
    @Override
    protected Product performOperation(Product product) {
        return productService.save(product);
    }
}