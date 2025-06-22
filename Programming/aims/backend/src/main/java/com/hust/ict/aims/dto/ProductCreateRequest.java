package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.MediaType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ProductCreateRequest {
    
    // Common Product Fields
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    private float value;
    
    @DecimalMin(value = "0.01", message = "Current price must be greater than 0")
    private float currentPrice;
    
    private String productDescription;
    
    @NotBlank(message = "Barcode is required")
    private String barcode;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
    
    @NotNull(message = "Warehouse entry date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warehouseEntryDate;
    
    @NotBlank(message = "Product dimensions are required")
    private String productDimensions;
    
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private float weight;
    
    private String imageURL;
    
    private boolean rushOrderEligible = true;
    
    private String genre;
    
    @NotNull(message = "Media type is required")
    private MediaType mediaType;
    
    // Book-specific fields
    private String author;
    private String coverType;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;
    private String language;
    private int numberOfPage;
    
    // CD/LP-specific fields
    private String album;
    private String artist;
    private String recordLabel;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private String tracklist;
    
    // DVD-specific fields
    private String director;
    private String studio;
    private String runtime;
    private String discType;
    private String subtitle;
    
    // Default constructor
    public ProductCreateRequest() {}
    
    // Constructor with required fields
    public ProductCreateRequest(String title, String category, float value, float currentPrice, 
                               String barcode, int quantity, LocalDate warehouseEntryDate, 
                               String productDimensions, float weight, MediaType mediaType) {
        this.title = title;
        this.category = category;
        this.value = value;
        this.currentPrice = currentPrice;
        this.barcode = barcode;
        this.quantity = quantity;
        this.warehouseEntryDate = warehouseEntryDate;
        this.productDimensions = productDimensions;
        this.weight = weight;
        this.mediaType = mediaType;
    }
    
    // Getters and Setters for Common Fields
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = value;
    }
    
    public float getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public LocalDate getWarehouseEntryDate() {
        return warehouseEntryDate;
    }
    
    public void setWarehouseEntryDate(LocalDate warehouseEntryDate) {
        this.warehouseEntryDate = warehouseEntryDate;
    }
    
    public String getProductDimensions() {
        return productDimensions;
    }
    
    public void setProductDimensions(String productDimensions) {
        this.productDimensions = productDimensions;
    }
    
    public float getWeight() {
        return weight;
    }
    
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    public String getImageURL() {
        return imageURL;
    }
    
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    
    public boolean isRushOrderEligible() {
        return rushOrderEligible;
    }
    
    public void setRushOrderEligible(boolean rushOrderEligible) {
        this.rushOrderEligible = rushOrderEligible;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public MediaType getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    // Getters and Setters for Book Fields
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCoverType() {
        return coverType;
    }
    
    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public int getNumberOfPage() {
        return numberOfPage;
    }
    
    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }
    
    // Getters and Setters for CD/LP Fields
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getRecordLabel() {
        return recordLabel;
    }
    
    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getTracklist() {
        return tracklist;
    }
    
    public void setTracklist(String tracklist) {
        this.tracklist = tracklist;
    }
    
    // Getters and Setters for DVD Fields
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public String getStudio() {
        return studio;
    }
    
    public void setStudio(String studio) {
        this.studio = studio;
    }
    
    public String getRuntime() {
        return runtime;
    }
    
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    
    public String getDiscType() {
        return discType;
    }
    
    public void setDiscType(String discType) {
        this.discType = discType;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    // Utility methods
    @Override
    public String toString() {
        return "ProductCreateRequest{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", value=" + value +
                ", currentPrice=" + currentPrice +
                ", mediaType=" + mediaType +
                ", barcode='" + barcode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
    
    // Validation helper methods
    public boolean isBook() {
        return MediaType.BOOK.equals(this.mediaType);
    }
    
    public boolean isCD() {
        return MediaType.CD.equals(this.mediaType);
    }
    
    public boolean isLP() {
        return MediaType.LP.equals(this.mediaType);
    }
    
    public boolean isDVD() {
        return MediaType.DVD.equals(this.mediaType);
    }
    
    public boolean isMusicMedia() {
        return isCD() || isLP();
    }
    
    // Price validation helper
    public boolean isPriceInValidRange() {
        float minPrice = this.value * 0.3f;
        float maxPrice = this.value * 1.5f;
        return this.currentPrice >= minPrice && this.currentPrice <= maxPrice;
    }
    
    // Validate required fields based on media type
    public boolean hasRequiredFields() {
        // Common required fields
        if (title == null || title.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            barcode == null || barcode.trim().isEmpty() ||
            productDimensions == null || productDimensions.trim().isEmpty() ||
            warehouseEntryDate == null ||
            mediaType == null ||
            value <= 0 || currentPrice <= 0 || weight <= 0 || quantity < 0) {
            return false;
        }
        
        // Media-specific required fields
        switch (mediaType) {
            case BOOK:
                return author != null && !author.trim().isEmpty() &&
                       publisher != null && !publisher.trim().isEmpty() &&
                       coverType != null && !coverType.trim().isEmpty() &&
                       publicationDate != null &&
                       language != null && !language.trim().isEmpty() &&
                       numberOfPage > 0;
                       
            case CD:
            case LP:
                return artist != null && !artist.trim().isEmpty() &&
                       album != null && !album.trim().isEmpty() &&
                       recordLabel != null && !recordLabel.trim().isEmpty();
                       
            case DVD:
                return director != null && !director.trim().isEmpty() &&
                       studio != null && !studio.trim().isEmpty() &&
                       runtime != null && !runtime.trim().isEmpty() &&
                       discType != null && !discType.trim().isEmpty() &&
                       language != null && !language.trim().isEmpty();
                       
            default:
                return false;
        }
    }
    
    // Calculate price with VAT (10%)
    public float getCurrentPriceWithVAT() {
        return this.currentPrice * 1.1f;
    }
    
    // Calculate value with VAT (10%)
    public float getValueWithVAT() {
        return this.value * 1.1f;
    }
}