package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.DVD;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component("dvdValidation")
public class DVDValidationStrategy implements ProductValidationStrategy {
    
    @Override
    public ValidationResult validate(Product product) {
        ValidationResult result = new ValidationResult();
        
        if (!(product instanceof DVD)) {
            result.addError("Product is not a DVD");
            return result;
        }
        
        DVD dvd = (DVD) product;
        
        if (dvd.getDirector() == null || dvd.getDirector().trim().isEmpty()) {
            result.addError("DVD director is required");
        }
        
        if (dvd.getStudio() == null || dvd.getStudio().trim().isEmpty()) {
            result.addError("DVD studio is required");
        }
        
        if (dvd.getRuntime() == null || dvd.getRuntime().trim().isEmpty()) {
            result.addError("DVD runtime is required");
        }
        
        if (dvd.getDiscType() == null || dvd.getDiscType().trim().isEmpty()) {
            result.addError("DVD disc type is required");
        }
        
        if (dvd.getLanguage() == null || dvd.getLanguage().trim().isEmpty()) {
            result.addError("DVD language is required");
        }
        
        return result;
    }
}