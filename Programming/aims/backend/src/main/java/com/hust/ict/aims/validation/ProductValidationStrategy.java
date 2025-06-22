package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.Product;

public interface ProductValidationStrategy {
    void validate(Product product);
    boolean supports(Product product);
}