package com.hust.ict.aims.validation;

import com.hust.ict.aims.model.DVD;
import com.hust.ict.aims.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class DVDValidationStrategy extends BaseProductValidationStrategy {
    
    // Valid disc types
    private static final List<String> VALID_DISC_TYPES = Arrays.asList(
        "DVD", "BLU-RAY", "BLURAY", "HD-DVD", "HDDVD"
    );
    
    // Runtime pattern (e.g., "2h 30m", "120 min", "90 minutes")
    private static final Pattern RUNTIME_PATTERN = Pattern.compile(
        "^(\\d{1,3}(h|hr|hour|hours)\\s*(\\d{1,2}(m|min|minute|minutes))?|\\d{1,3}\\s*(m|min|minute|minutes))$",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public void validate(Product product) {
        validateCommonFields(product);
        DVD dvd = (DVD) product;
        
        // Validate required DVD-specific fields
        if (dvd.getDirector() == null || dvd.getDirector().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DVD director is required");
        }
        
        if (dvd.getStudio() == null || dvd.getStudio().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DVD studio is required");
        }
        
        if (dvd.getRuntime() == null || dvd.getRuntime().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DVD runtime is required");
        }
        
        if (dvd.getDiscType() == null || dvd.getDiscType().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DVD disc type is required");
        }
        
        if (dvd.getLanguage() == null || dvd.getLanguage().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DVD language is required");
        }
        
        // Validate field formats and constraints
        validateDirectorName(dvd.getDirector());
        validateStudioName(dvd.getStudio());
        validateRuntime(dvd.getRuntime());
        validateDiscType(dvd.getDiscType());
        validateLanguage(dvd.getLanguage());
        
        // Validate optional fields if provided
        if (dvd.getReleaseDate() != null) {
            validateReleaseDate(dvd.getReleaseDate());
        }
        
        if (dvd.getSubtitle() != null && !dvd.getSubtitle().trim().isEmpty()) {
            validateSubtitle(dvd.getSubtitle());
        }
    }
    
    @Override
    public boolean supports(Product product) {
        return product instanceof DVD;
    }
    
    // Additional validation methods specific to DVDs
    private void validateDirectorName(String director) {
        if (director.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Director name cannot exceed 255 characters");
        }
        
        // Check for special characters that might cause issues
        if (director.matches(".*[<>\"'&].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Director name contains invalid characters");
        }
        
        // Director name should contain at least one letter
        if (!director.matches(".*[a-zA-Z].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Director name must contain at least one letter");
        }
    }
    
    private void validateStudioName(String studio) {
        if (studio.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Studio name cannot exceed 255 characters");
        }
        
        // Check for special characters that might cause issues
        if (studio.matches(".*[<>\"'&].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Studio name contains invalid characters");
        }
    }
    
    private void validateRuntime(String runtime) {
        if (runtime.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Runtime cannot exceed 50 characters");
        }
        
        // Validate runtime format
        if (!RUNTIME_PATTERN.matcher(runtime.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid runtime format. Use formats like '2h 30m', '120 min', or '90 minutes'");
        }
        
        // Extract and validate duration reasonableness
        validateRuntimeDuration(runtime);
    }
    
    private void validateRuntimeDuration(String runtime) {
        try {
            int totalMinutes = parseRuntimeToMinutes(runtime);
            
            if (totalMinutes < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Runtime must be at least 1 minute");
            }
            
            if (totalMinutes > 1440) { // 24 hours max
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Runtime cannot exceed 24 hours");
            }
            
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid runtime format");
        }
    }
    
    private int parseRuntimeToMinutes(String runtime) {
        String cleanRuntime = runtime.toLowerCase().trim();
        int totalMinutes = 0;
        
        // Parse hours and minutes
        if (cleanRuntime.contains("h")) {
            String[] parts = cleanRuntime.split("h");
            totalMinutes += Integer.parseInt(parts[0].trim()) * 60;
            
            if (parts.length > 1 && parts[1].trim().length() > 0) {
                String minutePart = parts[1].replaceAll("[^0-9]", "");
                if (!minutePart.isEmpty()) {
                    totalMinutes += Integer.parseInt(minutePart);
                }
            }
        } else {
            // Only minutes
            String minutePart = cleanRuntime.replaceAll("[^0-9]", "");
            totalMinutes = Integer.parseInt(minutePart);
        }
        
        return totalMinutes;
    }
    
    private void validateDiscType(String discType) {
        String normalizedDiscType = discType.toUpperCase().trim();
        
        if (!VALID_DISC_TYPES.contains(normalizedDiscType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid disc type. Valid types are: " + String.join(", ", VALID_DISC_TYPES));
        }
    }
    
    private void validateLanguage(String language) {
        if (language.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Language cannot exceed 255 characters");
        }
        
        // Language should contain only letters, spaces, and common language separators
        if (!language.matches("^[a-zA-Z\\s,;/\\-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Language contains invalid characters. Use only letters, spaces, commas, semicolons, slashes, and hyphens");
        }
        
        // Check for reasonable language format (e.g., "English", "English, Spanish", "English/Spanish")
        String[] languages = language.split("[,;/]");
        for (String lang : languages) {
            if (lang.trim().length() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Each language must be at least 2 characters long");
            }
            if (lang.trim().length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Individual language names cannot exceed 50 characters");
            }
        }
    }
    
    private void validateSubtitle(String subtitle) {
        if (subtitle.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Subtitle information cannot exceed 500 characters");
        }
        
        // Subtitle should contain only letters, spaces, and common separators
        if (!subtitle.matches("^[a-zA-Z\\s,;/\\-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Subtitle contains invalid characters. Use only letters, spaces, commas, semicolons, slashes, and hyphens");
        }
    }
    
    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate now = LocalDate.now();
        LocalDate earliestDate = LocalDate.of(1950, 1, 1); // DVDs invented in 1995, but allow some flexibility
        
        if (releaseDate.isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "DVD release date cannot be in the future");
        }
        
        if (releaseDate.isBefore(earliestDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "DVD release date cannot be before 1950");
        }
    }
}