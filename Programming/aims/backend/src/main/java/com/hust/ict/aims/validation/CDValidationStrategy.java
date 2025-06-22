package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.CD;
import com.hust.ict.aims.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;

@Component
public class CDValidationStrategy extends BaseProductValidationStrategy {
    
    @Override
    public void validate(Product product) {
        validateCommonFields(product);
        CD cd = (CD) product;
        
        // Validate required CD-specific fields
        if (cd.getArtist() == null || cd.getArtist().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CD artist is required");
        }
        
        if (cd.getAlbum() == null || cd.getAlbum().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CD album is required");
        }
        
        if (cd.getRecordLabel() == null || cd.getRecordLabel().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record label is required for CDs");
        }
        
        // Validate optional fields if provided
        if (cd.getReleaseDate() != null) {
            validateReleaseDate(cd.getReleaseDate());
        }
        
        if (cd.getTracklist() != null && cd.getTracklist().trim().length() > 0) {
            validateTracklist(cd.getTracklist());
        }
        
        // Validate artist name format
        validateArtistName(cd.getArtist());
        
        // Validate album name format
        validateAlbumName(cd.getAlbum());
        
        // Validate record label format
        validateRecordLabel(cd.getRecordLabel());
    }
    
    @Override
    public boolean supports(Product product) {
        return product instanceof CD;
    }
    
    // Additional validation methods specific to CDs
    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate now = LocalDate.now();
        LocalDate earliestDate = LocalDate.of(1900, 1, 1); // CDs invented around 1982, but allow some flexibility
        
        if (releaseDate.isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "CD release date cannot be in the future");
        }
        
        if (releaseDate.isBefore(earliestDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "CD release date cannot be before 1900");
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