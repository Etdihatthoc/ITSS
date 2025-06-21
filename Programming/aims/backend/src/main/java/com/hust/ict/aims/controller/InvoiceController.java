package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.InvoiceRequestDTO;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.model.Invoice;
import com.hust.ict.aims.repository.CartRepository;
import com.hust.ict.aims.repository.InvoiceRepository;
import com.hust.ict.aims.service.InvoiceService;
import com.hust.ict.aims.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:5173")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CartRepository cartRepository;

    private final InvoiceService invoiceService;
    private final CartService cartService;

    public InvoiceController(InvoiceService invoiceService, CartService cartService) {
        this.invoiceService = invoiceService;
        this.cartService = cartService;
    }

    @GetMapping
    public List<Invoice> findAll() {
        return invoiceService.findAll();
    }

    @GetMapping("/{id}")
    public Invoice findById(@PathVariable int id) {
        return invoiceService.findById(id);
    }


    @PostMapping("/cart/{cartId}/delivery/{deliveryId}")
    public ResponseEntity<?> createInvoice(@PathVariable long cartId,
                                           @PathVariable long deliveryId) {

        Invoice savedInvoice = invoiceService.save(cartId, deliveryId);
        return ResponseEntity.created(URI.create("/api/invoices/" + savedInvoice.getId())).body(savedInvoice);
    }

    @PutMapping("/{id}/cart/{cartId}/delivery/{deliveryId}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable int id,
                                                 @PathVariable long cartId,
                                                 @PathVariable long deliveryId) {
        Invoice updated = invoiceService.update(id, cartId, deliveryId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        invoiceService.deleteById(id);
    }
}
