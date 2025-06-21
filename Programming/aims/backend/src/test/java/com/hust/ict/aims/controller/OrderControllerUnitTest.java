//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.dto.OrderRequestDTO;
//import com.hust.ict.aims.model.*;
//import com.hust.ict.aims.repository.DeliveryInfoRepository;
//import com.hust.ict.aims.repository.InvoiceRepository;
//import com.hust.ict.aims.repository.OrderRepository;
//import com.hust.ict.aims.repository.TransactionRepository;
//import com.hust.ict.aims.service.OrderService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(OrderController.class)
//class OrderControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private OrderService orderService;
//
//    @MockitoBean
//    private TransactionRepository transactionRepository;
//
//    @MockitoBean
//    private InvoiceRepository invoiceRepository;
//
//    @MockitoBean
//    private DeliveryInfoRepository deliveryInfoRepository;
//
//    @MockitoBean
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testFindAllOrders() throws Exception {
//        Orders order = new Orders();
//        order.setId(1L);
//
//        when(orderService.findAll()).thenReturn(List.of(order));
//
//        mockMvc.perform(get("/api/orders"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void testFindOrderById_Found() throws Exception {
//        Orders order = new Orders();
//        order.setId(1L);
//
//        when(orderService.findById(1L)).thenReturn(order);
//
//        mockMvc.perform(get("/api/orders/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testFindOrderById_NotFound() throws Exception {
//        when(orderService.findById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/orders/999"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(""));
//    }
//
//    @Test
//    void testCreateOrder_Success() throws Exception {
//        OrderRequestDTO request = new OrderRequestDTO();
//        request.setTransactionId(1);
//        request.setInvoiceId(2);
//        request.setDeliveryId(3);
//        request.setStatus("PROCESSING");
//
//        Transaction transaction = new Transaction();
//        Invoice invoice = new Invoice();
//        DeliveryInfo deliveryInfo = new DeliveryInfo();
//
//        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
//        when(invoiceRepository.findById(2)).thenReturn(Optional.of(invoice));
//        when(deliveryInfoRepository.findById(3L)).thenReturn(Optional.of(deliveryInfo));
//
//        Orders savedOrder = new Orders(transaction, invoice, deliveryInfo, "PROCESSING");
//        savedOrder.setId(1L);
//
//        when(orderRepository.save(any(Orders.class))).thenReturn(savedOrder);
//
//        mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.status").value("PROCESSING"));
//    }
//
//    @Test
//    void testCreateOrder_BadRequest() throws Exception {
//        OrderRequestDTO request = new OrderRequestDTO();
//        request.setTransactionId(1);
//        request.setInvoiceId(2);
//        request.setDeliveryId(3);
//        request.setStatus("FAILED");
//
//        when(transactionRepository.findById(1)).thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testUpdateOrder() throws Exception {
//        Orders order = new Orders();
//        order.setId(1L);
//        order.setStatus("SHIPPED");
//
//        when(orderService.save(any(Orders.class))).thenReturn(order);
//
//        mockMvc.perform(put("/api/orders/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(order)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.status").value("SHIPPED"));
//    }
//
//    @Test
//    void testDeleteOrder() throws Exception {
//        doNothing().when(orderService).deleteById(1L);
//
//        mockMvc.perform(delete("/api/orders/1"))
//                .andExpect(status().isOk());
//    }
//}
