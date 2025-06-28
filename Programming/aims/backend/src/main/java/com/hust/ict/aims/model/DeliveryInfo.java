package com.hust.ict.aims.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


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
@Setter
@Getter
@Table(name = "deliveryinfo")
public class DeliveryInfo {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryid")
    private Long id;

    @Getter
    @Column(name = "deliveryaddress", nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String province;

    @Column()
    private String district;

    @Column(name = "phonenumber", nullable = false)
    private String phoneNumber;

    @Column(name = "recipientname", nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String email;
}
