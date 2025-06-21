package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.CheckoutRequest;
import com.hust.ict.aims.dto.OrderRequestDTO;
import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.model.Invoice;
import com.hust.ict.aims.model.Orders;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.repository.DeliveryInfoRepository;
import com.hust.ict.aims.repository.InvoiceRepository;
import com.hust.ict.aims.repository.OrderRepository;
import com.hust.ict.aims.repository.TransactionRepository;
import com.hust.ict.aims.service.DeliveryInfoService;
import com.hust.ict.aims.service.InvoiceService;
import com.hust.ict.aims.service.OrderService;
import com.hust.ict.aims.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private final TransactionRepository transactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final OrderRepository orderRepository;
    private final DeliveryInfoService deliveryInfoService;
    private final InvoiceService invoiceService;
    private final TransactionService transactionService;

    public OrderController(OrderService orderService, TransactionRepository transactionRepository, InvoiceRepository invoiceRepository, DeliveryInfoRepository deliveryInfoRepository, OrderRepository orderRepository, DeliveryInfoService deliveryInfoService, InvoiceService invoiceService, TransactionService transactionService) {
        this.orderService = orderService;
        this.transactionRepository = transactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.orderRepository = orderRepository;
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

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        Transaction transaction = transactionRepository.findById(orderRequest.getTransactionId()).orElse(null);
        Invoice invoice = invoiceRepository.findById(orderRequest.getInvoiceId()).orElse(null);
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(Long.valueOf(orderRequest.getDeliveryId())).orElse(null);

        if (transaction == null || invoice == null || deliveryInfo == null) {
            return ResponseEntity.badRequest().body("Invalid references to transaction, invoice, or delivery info");
        }

        Orders order = new Orders(transaction, invoice, deliveryInfo, orderRequest.getStatus());
        Orders savedOrder = orderRepository.save(order);

        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("checkout/create-order")
    public ResponseEntity<?> completeCheckout(@RequestBody CheckoutRequest request) {
        // 1. Create delivery info
        DeliveryInfo deliveryInfo = deliveryInfoService.save(request.getDeliveryInfo());

        // 2. Create invoice from invoice data and cart data
        Invoice invoice = invoiceService.createInvoice(request.getInvoiceData());

        // 3. Create transaction
        Transaction transaction = transactionService.createTransaction(request.getTransactionData());


        if (transaction == null || invoice == null || deliveryInfo == null) {
            return ResponseEntity.badRequest().body("Invalid references to transaction, invoice, or delivery info");
        }


        // 4. Create order

        Orders order = new Orders(transaction, invoice, deliveryInfo, request.getStatus());
        Orders savedOrder = orderRepository.save(order);

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
}
