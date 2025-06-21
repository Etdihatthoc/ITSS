//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.dto.InvoiceRequestDTO;
//import com.hust.ict.aims.model.Cart;
//import com.hust.ict.aims.model.Invoice;
//import com.hust.ict.aims.repository.CartRepository;
//import com.hust.ict.aims.repository.InvoiceRepository;
//import com.hust.ict.aims.service.InvoiceService;
//import com.hust.ict.aims.service.CartService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(InvoiceController.class)
//class InvoiceControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockitoBean
//    private InvoiceService invoiceService;
//
//    @MockitoBean
//    private CartService cartService;
//
//    @MockitoBean
//    private CartRepository cartRepository;
//
//    @MockitoBean
//    private InvoiceRepository invoiceRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testFindAllInvoices() throws Exception {
//        Invoice invoice = new Invoice();
//        invoice.setId(1);
//
//        when(invoiceService.findAll()).thenReturn(List.of(invoice));
//
//        mockMvc.perform(get("/api/invoices"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void testFindInvoiceById_Found() throws Exception {
//        Invoice invoice = new Invoice();
//        invoice.setId(1);
//
//        when(invoiceService.findById(1)).thenReturn(invoice);
//
//        mockMvc.perform(get("/api/invoices/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testFindInvoiceById_NotFound() throws Exception {
//        when(invoiceService.findById(999)).thenReturn(null);
//
//        mockMvc.perform(get("/api/invoices/999"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(""));
//    }
//
//    @Test
//    void testCreateInvoice_Success() throws Exception {
//        InvoiceRequestDTO dto = new InvoiceRequestDTO();
//        dto.setCartId(1);
//        dto.setTotalProductPriceAfterVAT(100.0F);
//        dto.setTotalAmount(110.0F);
//        dto.setDeliveryFee(10.0F);
//
//        Cart cart = new Cart();
//        cart.setId(1L);
//
//        Invoice savedInvoice = new Invoice(cart, 100.0F, 110.0F, 10.0F);
//        savedInvoice.setId(1);
//
//        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);
//
//        mockMvc.perform(post("/api/invoices")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/api/invoices/1"))
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testCreateInvoice_CartNotFound() throws Exception {
//        InvoiceRequestDTO dto = new InvoiceRequestDTO();
//        dto.setCartId(999);
//
//        when(cartRepository.findById(999L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/invoices")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testUpdateInvoice() throws Exception {
//        Invoice invoice = new Invoice();
//        invoice.setId(1);
//        invoice.setTotalAmount(200);
//
//        when(invoiceService.update(eq(1), eq(2L), eq(3L))).thenReturn(invoice);
//
//
//        mockMvc.perform(put("/api/invoices/1/cart/2/delivery/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.totalAmount").value(200.0));
//    }
//
//    @Test
//    void testDeleteInvoice() throws Exception {
//        doNothing().when(invoiceService).deleteById(1);
//
//        mockMvc.perform(delete("/api/invoices/1"))
//                .andExpect(status().isOk());
//    }
//}
