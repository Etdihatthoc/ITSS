package com.hust.ict.aims.service;

import com.hust.ict.aims.dto.InvoiceRequest;
import com.hust.ict.aims.model.Invoice;

import java.util.List;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All methods are tightly focused on managing Invoice persistence.
// No unrelated logic such as billing rules, validation, or controller responsibilities.
//
// SRP COMMENT:
// Single Responsibility: This service is solely responsible for CRUD operations on Invoice entities.
// It does not handle payment processing, reporting, or business rules beyond repository delegation.
// ------------------------------------------------------------

public interface InvoiceService {
    List<Invoice> findAll();
    Invoice findById(int id);
    Invoice save(Long cartId, Long deliveryId);
    void deleteById(int id);
    Invoice update(int invoiceId, Long cartId, Long deliveryId);
    Invoice createInvoice(InvoiceRequest invoiceData);
}
