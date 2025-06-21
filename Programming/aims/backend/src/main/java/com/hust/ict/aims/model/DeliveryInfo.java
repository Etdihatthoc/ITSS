package com.hust.ict.aims.model;

import jakarta.persistence.*;


// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All fields and methods are directly related to managing delivery contact and address info.
// There is no unrelated logic (e.g., validation, shipping cost calculation, formatting).
//
// SRP COMMENT:
// Single Responsibility: This class only encapsulates delivery recipient and address information.
// It does not handle shipping decisions, order linkage, or pricing.
// ------------------------------------------------------------

@Entity
@Table(name = "deliveryinfo")
public class DeliveryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryid")
    private Long id;

    @Column(name = "deliveryaddress", nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String province;

    @Column(name = "phonenumber", nullable = false)
    private String phoneNumber;

    @Column(name = "recipientname", nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String email;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
