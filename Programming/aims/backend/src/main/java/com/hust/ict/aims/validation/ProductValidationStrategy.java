package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.Product;

public interface ProductValidationStrategy {
    ValidationResult validate(Product product);
}