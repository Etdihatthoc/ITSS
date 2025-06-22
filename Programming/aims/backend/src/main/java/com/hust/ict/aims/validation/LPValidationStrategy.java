package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.LP;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Component;

@Component("lpValidation")
public class LPValidationStrategy implements ProductValidationStrategy {
    
    @Override
    public ValidationResult validate(Product product) {
        ValidationResult result = new ValidationResult();
        
        if (!(product instanceof LP)) {
            result.addError("Product is not an LP");
            return result;
        }
        
        LP lp = (LP) product;
        
        if (lp.getArtist() == null || lp.getArtist().trim().isEmpty()) {
            result.addError("LP artist is required");
        }
        
        if (lp.getRecordLabel() == null || lp.getRecordLabel().trim().isEmpty()) {
            result.addError("LP record label is required");
        }
        
        if (lp.getTracklist() == null || lp.getTracklist().trim().isEmpty()) {
            result.addError("LP tracklist is required");
        }
        
        if (lp.getAlbum() == null || lp.getAlbum().trim().isEmpty()) {
            result.addError("LP album name is required");
        }
        
        return result;
    }
}