//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.model.Product;
//import com.hust.ict.aims.service.OperationService;
//import com.hust.ict.aims.service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ProductController.class)
//public class ProductControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private ProductService productService;
//
//    @MockitoBean
//    private OperationService operationService;
//
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Product product;
//
//    @BeforeEach
//    public void setUp() {
//        product = new Product();
//        product.setId(1L);
//        product.setTitle("Wireless Mouse");
//        product.setCategory("Electronics");
//        product.setValue(29.99f);
//        product.setCurrentPrice(19.99f);
//        product.setImageURL("https://example.com/product-image.jpg");
//        product.setRushOrderEligible(true);
//        product.setWeight(1.5f);
//        product.setProductDimensions("10x5x3 inches");
//        product.setWarehouseEntryDate(LocalDate.of(2025, 5, 3));
//        product.setBarcode("123456789030");
//        product.setProductDescription("A high-quality wireless mouse with ergonomic design.");
//        product.setQuantity(150);
//        product.setGenre("Technology");
//    }
//
//    @Test
//    public void testSaveProduct() throws Exception {
//        when(productService.save(any(Product.class))).thenReturn(product);
//
//        mockMvc.perform(post("/api/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Wireless Mouse"));
//    }
//
//    @Test
//    public void testGetProductById() throws Exception {
//        when(productService.findById(1L)).thenReturn(product);
//
//        mockMvc.perform(get("/api/products/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Wireless Mouse"));
//    }
//
//    @Test
//    public void testUpdateProduct() throws Exception {
//        Product updated = new Product();
//        updated.setId(1L);
//        updated.setTitle("Updated Title");
//        updated.setCategory("Electronics");
//        updated.setValue(29.99f);
//        updated.setCurrentPrice(17.99f);
//        updated.setImageURL("https://example.com/product-image.jpg");
//        updated.setRushOrderEligible(true);
//        updated.setWeight(1.5f);
//        updated.setProductDimensions("10x5x3 inches");
//        updated.setWarehouseEntryDate(LocalDate.of(2025, 5, 3));
//        updated.setBarcode("123456789030");
//        updated.setProductDescription("Updated description");
//        updated.setQuantity(100);
//        updated.setGenre("Technology");
//
//        when(productService.findById(1L)).thenReturn(product);
//        when(productService.update(eq(1L), any(Product.class))).thenReturn(updated);
//
//        mockMvc.perform(put("/api/products/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updated)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Updated Title"))
//                .andExpect(jsonPath("$.currentPrice").value(17.99));
//    }
//
//    @Test
//    public void testDeleteProduct() throws Exception {
//        doNothing().when(productService).delete(1L);
//
//        mockMvc.perform(delete("/api/products/1"))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).delete(1L);
//    }
//
//    @Test
//    public void testGetAllProducts() throws Exception {
//        when(productService.findAll()).thenReturn(List.of(product));
//
//        mockMvc.perform(get("/api/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].title").value("Wireless Mouse"));
//    }
//
//    @Test
//    public void testGetProductNotFound() throws Exception {
//        when(productService.findById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/products/999"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testUpdateProductNotFound() throws Exception {
//        when(productService.findById(999L)).thenReturn(null);
//
//        mockMvc.perform(put("/api/products/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(status().isNotFound());
//    }
//}
