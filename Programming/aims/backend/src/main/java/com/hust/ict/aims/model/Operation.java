package com.hust.ict.aims.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
/* Cohesion Level: Functional Cohesion
 * Similarly demonstrates functional cohesion, with all members focused on representing an operation performed on a product. The timestamp, operation type, and product reference all support this single purpose.
*/
@Entity
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationID;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String operationType;
    private LocalDateTime timestamp;

    // Constructors
    public Operation() {
        // JPA requires a no-args constructor (public or protected) :contentReference[oaicite:2]{index=2}
    }

    public Operation(Product product, String operationType, LocalDateTime timestamp) {
        this.product = product;
        this.operationType = operationType;
        this.timestamp = timestamp;
    }
    // Getters and setters
    public Long getOperationID() {
        return operationID;
    }

    public void setOperationID(Long operationID) {
        this.operationID = operationID;
    }

    // Getter và Setter cho product
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Getter và Setter cho operationType
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    // Getter và Setter cho timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
