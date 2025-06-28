package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.*;
import com.hust.ict.aims.model.*;
import com.hust.ict.aims.service.DeliveryInfoService;
import com.hust.ict.aims.service.InvoiceService;
import com.hust.ict.aims.service.RushOrderService;
import com.hust.ict.aims.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rush-orders")
@CrossOrigin(origins = "http://localhost:5173")
public class RushOrderController {
    private final RushOrderService rushOrderService;
    private final TransactionService transactionService;
    private final DeliveryInfoService deliveryInfoService;
    private final InvoiceService invoiceService;

    public RushOrderController(RushOrderService rushOrderService, TransactionService transactionService, DeliveryInfoService deliveryInfoService, InvoiceService invoiceService) {
        this.rushOrderService = rushOrderService;
        this.transactionService = transactionService;
        this.deliveryInfoService = deliveryInfoService;
        this.invoiceService = invoiceService;
    }

    @GetMapping()
    public List<RushOrder> getAllRushOrders() {
        return rushOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RushOrder> getRushOrderById(@PathVariable Long id) {
        RushOrder rushOrder = rushOrderService.findById(id);
        if (rushOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rushOrder);
    }

    @PostMapping()
    public ResponseEntity<RushOrder> saveRushOrder(@RequestBody RushCheckoutRequestDTO rushCheckoutRequestDTO) {
        Transaction transaction = transactionService.createTransaction(rushCheckoutRequestDTO.getTransactionRequest());
        Invoice invoice = invoiceService.createInvoice(rushCheckoutRequestDTO.getInvoiceRequest());
        DeliveryInfo deliveryInfo = deliveryInfoService.save(rushCheckoutRequestDTO.getDeliveryInfo());
        RushOrder rushOrder = new RushOrder(transaction, invoice, deliveryInfo, rushCheckoutRequestDTO.getStatus(), rushCheckoutRequestDTO.getDeliveryTime(), rushCheckoutRequestDTO.getDeliveryInstruction());
        RushOrder savedRushOrder = rushOrderService.save(rushOrder );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRushOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RushOrder> updateRushOrder(@PathVariable Long id, @RequestBody RushOrderDTO rushOrderDTO) {
        RushOrder existing = rushOrderService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setDeliveryInstruction(rushOrderDTO.getDeliveryInstruction());
        existing.setDeliveryTime(rushOrderDTO.getDeliveryTime());

        RushOrder updated = rushOrderService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRushOrder(@PathVariable Long id) {
        RushOrder existingRushOrder = rushOrderService.findById(id);
        if (existingRushOrder == null) {
            return ResponseEntity.notFound().build();
        }

        rushOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-eligibility")
    public ResponseEntity<?> checkEligibility(@RequestBody RushEligibilityRequestDTO rushOrderEligibilityRequestDTO) {
        if(!rushOrderService.isSupportedAddress(rushOrderEligibilityRequestDTO.getDeliveryInfoDTO())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not supported address"));
        }
        if(!rushOrderService.isAnySupportedItem(rushOrderEligibilityRequestDTO.getCartRequestDTO())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not supported items"));
        }
        return ResponseEntity.ok().build();
    }
}
