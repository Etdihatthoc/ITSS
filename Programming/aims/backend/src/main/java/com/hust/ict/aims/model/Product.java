package com.hust.ict.aims.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

/* Cohesion Level: Functional Cohesion
 * Exhibits functional cohesion as all members contribute to the single responsibility of representing a product. The class manages product attributes like ID, title, price, and quantity, with all methods directly related to these attributes.
*/

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "product")
// Jackson annotations for polymorphic deserialization
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "productType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "BOOK"),
        @JsonSubTypes.Type(value = CD.class, name = "CD"),
        @JsonSubTypes.Type(value = DVD.class, name = "DVD"),
        @JsonSubTypes.Type(value = LP.class, name = "LP")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String imageURL;

    @Column(nullable = false)
    private boolean rushOrderEligible;

    @Column(nullable = false)
    private float weight;

    @Column(nullable = false)
    private String productDimensions;

    @Column(nullable = false)
    private LocalDate warehouseEntryDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private float value;

    @Column(nullable = false)
    private float currentPrice;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(length = 2000)
    private String productDescription;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Virtual field for JSON deserialization - not persisted to database
    @Transient
    private String productType;
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Virtual productType getter/setter for JSON binding
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getProductDimensions() {
        return productDimensions;
    }

    public void setProductDimensions(String productDimensions) {
        this.productDimensions = productDimensions;
    }

    public LocalDate getWarehouseEntryDate() {
        return warehouseEntryDate;
    }

    public void setWarehouseEntryDate(LocalDate warehouseEntryDate) {
        this.warehouseEntryDate = warehouseEntryDate;
    }

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}