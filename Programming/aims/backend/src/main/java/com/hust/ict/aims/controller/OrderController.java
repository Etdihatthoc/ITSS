package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.CheckoutRequest;
import com.hust.ict.aims.dto.OrderStatusUpdateDTO;
import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.model.Invoice;
import com.hust.ict.aims.model.Orders;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.service.DeliveryInfoService;
import com.hust.ict.aims.service.InvoiceService;
import com.hust.ict.aims.service.OrderService;
import com.hust.ict.aims.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private final DeliveryInfoService deliveryInfoService;
    private final InvoiceService invoiceService;
    private final TransactionService transactionService;

    public OrderController(OrderService orderService, DeliveryInfoService deliveryInfoService,
                           InvoiceService invoiceService, TransactionService transactionService) {
        this.orderService = orderService;
        this.deliveryInfoService = deliveryInfoService;
        this.invoiceService = invoiceService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Orders> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Orders findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping("checkout/create-order")
    public ResponseEntity<?> completeCheckout(@RequestBody CheckoutRequest request) {
        // 1. Create delivery info
        DeliveryInfo deliveryInfo = deliveryInfoService.save(request.getDeliveryInfo());

        // 2. Create invoice from invoice data and cart data
        Invoice invoice = invoiceService.createInvoice(request.getInvoiceData());

//      3. Create transaction
//      Transaction transaction = transactionService.createTransaction(request.getTransactionData());

        Transaction transaction = transactionService.getTransaction(request.getTransactionData());

        if (transaction == null || invoice == null || deliveryInfo == null) {
            System.err.println("Invalid references to transaction, invoice, or delivery info");
            return ResponseEntity.badRequest().body("Invalid references to transaction, invoice, or delivery info");
        }

        // 4. Create order

        Orders order = new Orders(transaction, invoice, deliveryInfo, request.getStatus());
        Orders savedOrder = orderService.save(order);

        // 5. Return the complete order with all relations
        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("/{id}")
    public Orders update(@PathVariable Long id, @RequestBody Orders order) {
        order.setId(id);
        return orderService.save(order);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.deleteById(id);
    }

    /**
     * Update an order's status
     * @param id Order ID
     * @param statusUpdateDTO Status update data
     * @return Updated order
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Orders> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {

        Orders updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Auto-reject pending orders with insufficient stock
     *
     * @return List of order IDs that were auto-rejected
     */
    @PostMapping("/auto-reject-insufficient-stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> autoRejectOrdersWithInsufficientStock() {
        List<Long> rejectedOrderIds = orderService.rejectOrdersWithInsufficientStock();

        Map<String, Object> response = new HashMap<>();
        response.put("rejectedOrderIds", rejectedOrderIds);
        response.put("count", rejectedOrderIds.size());

        return ResponseEntity.ok(response);
    }
}
