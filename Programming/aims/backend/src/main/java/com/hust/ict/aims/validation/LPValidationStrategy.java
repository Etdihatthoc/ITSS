package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.LP;
import com.hust.ict.aims.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;

@Component
public class LPValidationStrategy extends BaseProductValidationStrategy {
    
    @Override
    public void validate(Product product) {
        validateCommonFields(product);
        LP lp = (LP) product;
        
        // Validate required LP-specific fields
        if (lp.getArtist() == null || lp.getArtist().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LP artist is required");
        }
        
        if (lp.getAlbum() == null || lp.getAlbum().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LP album is required");
        }
        
        if (lp.getRecordLabel() == null || lp.getRecordLabel().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record label is required for LPs");
        }
        
        // Validate optional fields if provided
        if (lp.getReleaseDate() != null) {
            validateReleaseDate(lp.getReleaseDate());
        }
        
        if (lp.getTracklist() != null && lp.getTracklist().trim().length() > 0) {
            validateTracklist(lp.getTracklist());
        }
        
        // Validate artist name format
        validateArtistName(lp.getArtist());
        
        // Validate album name format
        validateAlbumName(lp.getAlbum());
        
        // Validate record label format
        validateRecordLabel(lp.getRecordLabel());
    }
    
    @Override
    public boolean supports(Product product) {
        return product instanceof LP;
    }
    
    // Additional validation methods specific to LPs
    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate now = LocalDate.now();
        LocalDate earliestDate = LocalDate.of(1890, 1, 1); // LP records invented in late 1800s
        
        if (releaseDate.isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "LP release date cannot be in the future");
        }
        
        if (releaseDate.isBefore(earliestDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "LP release date cannot be before 1890");
        }
    }
    
    private void validateTracklist(String tracklist) {
        if (tracklist.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Tracklist is too long (maximum 2000 characters)");
        }
        
        // Check if tracklist has at least one track
        String[] tracks = tracklist.split(",");
        if (tracks.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Tracklist must contain at least one track");
        }
        
        // LPs typically have fewer tracks than CDs, validate reasonable range
        if (tracks.length > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "LP cannot have more than 30 tracks (typical LP limit)");
        }
        
        // Validate each track name
        for (String track : tracks) {
            if (track.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Tracklist cannot contain empty track names");
            }
            if (track.trim().length() > 100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Individual track names cannot exceed 100 characters");
            }
        }
    }
    
    private void validateArtistName(String artist) {
        if (artist.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Artist name cannot exceed 255 characters");
        }
        
        // Check for special characters that might cause issues
        if (artist.matches(".*[<>\"'&].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Artist name contains invalid characters");
        }
    }
    
    private void validateAlbumName(String album) {
        if (album.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album name cannot exceed 255 characters");
        }
        
        // Check for special characters that might cause issues
        if (album.matches(".*[<>\"'&].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album name contains invalid characters");
        }
    }
    
    private void validateRecordLabel(String recordLabel) {
        if (recordLabel.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Record label name cannot exceed 255 characters");
        }
        
        // Check for special characters that might cause issues
        if (recordLabel.matches(".*[<>\"'&].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Record label name contains invalid characters");
        }
    }
}