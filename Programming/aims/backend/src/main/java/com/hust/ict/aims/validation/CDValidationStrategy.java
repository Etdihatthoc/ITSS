package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.CD;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component("cdValidation")
public class CDValidationStrategy implements ProductValidationStrategy {
    
    @Override
    public ValidationResult validate(Product product) {
        ValidationResult result = new ValidationResult();
        
        if (!(product instanceof CD)) {
            result.addError("Product is not a CD");
            return result;
        }
        
        CD cd = (CD) product;
        
        if (cd.getArtist() == null || cd.getArtist().trim().isEmpty()) {
            result.addError("CD artist is required");
        }
        
        if (cd.getRecordLabel() == null || cd.getRecordLabel().trim().isEmpty()) {
            result.addError("CD record label is required");
        }
        
        if (cd.getTracklist() == null || cd.getTracklist().trim().isEmpty()) {
            result.addError("CD tracklist is required");
        }
        
        if (cd.getAlbum() == null || cd.getAlbum().trim().isEmpty()) {
            result.addError("CD album name is required");
        }
        
        return result;
    }
}