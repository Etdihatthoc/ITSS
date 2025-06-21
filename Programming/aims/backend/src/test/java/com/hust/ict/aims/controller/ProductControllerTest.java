//package com.hust.ict.aims.controller;
//
//import com.hust.ict.aims.dto.ProductDTO;
//import com.hust.ict.aims.model.Book;
//import com.hust.ict.aims.service.OperationService;
//import com.hust.ict.aims.service.ProductService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.util.Collections;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//@WebMvcTest(ProductController.class)
//class ProductControllerTest {
//
//    @Autowired
//    private ProductController controller;
//
//    @MockitoBean
//    private ProductService productService;
//
//    @MockitoBean
//    private OperationService operationService;
//
//    @Test
//    void whenGetProductById_thenReturnProductDTO() {
//        // Mock data
//        Book book = new Book();
//        book.setId(1L);
//        book.setTitle("Clean Code");
//        book.setImageURL("image.jpg");
//        book.setRushOrderEligible(true);
//        book.setWeight(0.5f);
//        book.setProductDimensions("5x7");
//        book.setCategory("Programming");
//        book.setValue(50.0f);
//        book.setCurrentPrice(45.0f);
//        book.setBarcode("123456789");
//        book.setQuantity(10);
//        Mockito.when(productService.findById(1L)).thenReturn(book);
//
//        // Gọi API
//        ResponseEntity<ProductDTO> response = controller.findById(1L);
//
//        // Kiểm tra kết quả
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Clean Code", response.getBody().getTitle());
//        assertEquals("BOOK", response.getBody().getProductType());
//    }
//
//    @Test
//    void whenGetNonExistentProduct_thenReturnNotFound() {
//        Mockito.when(productService.findById(999L)).thenReturn(null);
//
//        ResponseEntity<ProductDTO> response = controller.findById(999L);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void whenGetAllProducts_thenReturnListDTO() {
//        Book book = new Book();
//        book.setId(1L);
//        book.setTitle("Clean Code");
//        book.setImageURL("image.jpg");
//        book.setRushOrderEligible(true);
//        book.setWeight(0.5f);
//        book.setProductDimensions("5x7");
//        book.setCategory("Programming");
//        book.setValue(50.0f);
//        book.setCurrentPrice(45.0f);
//        book.setBarcode("123456789");
//        book.setQuantity(10);
//        Mockito.when(productService.findAll()).thenReturn(Collections.singletonList(book));
//
//        List<ProductDTO> dtos = controller.findAll();
//
//        assertEquals(1, dtos.size());
//        assertEquals("BOOK", dtos.getFirst().getProductType());
//    }
//}
