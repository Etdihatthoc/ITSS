package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.Book;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component("bookValidation")
public class BookValidationStrategy implements ProductValidationStrategy {
    
    @Override
    public ValidationResult validate(Product product) {
        ValidationResult result = new ValidationResult();
        
        if (!(product instanceof Book)) {
            result.addError("Product is not a Book");
            return result;
        }
        
        Book book = (Book) product;
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            result.addError("Book author is required");
        }
        
        if (book.getCoverType() == null || book.getCoverType().trim().isEmpty()) {
            result.addError("Book cover type is required");
        }
        
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            result.addError("Book publisher is required");
        }
        
        if (book.getPublicationDate() == null) {
            result.addError("Book publication date is required");
        }
        
        if (book.getLanguage() == null || book.getLanguage().trim().isEmpty()) {
            result.addError("Book language is required");
        }
        
        if (book.getNumberOfPage() <= 0) {
            result.addError("Book must have positive number of pages");
        }
        
        return result;
    }
}