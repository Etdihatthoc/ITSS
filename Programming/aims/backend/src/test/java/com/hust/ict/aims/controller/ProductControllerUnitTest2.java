//package com.hust.ict.aims.controller;
//
//import com.hust.ict.aims.dto.ProductDTO;
//import com.hust.ict.aims.model.DVD;
//import com.hust.ict.aims.model.Product;
//import com.hust.ict.aims.service.ProductService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ProductControllerUnitTest2 {
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private ProductController controller;
//
//    @Test
//    void whenProductExists_thenReturn200AndBody() {
//        // Chuẩn bị dữ liệu giả (mock)
//        Product mockProduct = new Product();
//        mockProduct.setId(1L);
//        mockProduct.setTitle("Test Product");
//
//        // Định nghĩa hành vi của mock
//        when(productService.findById(1L)).thenReturn(mockProduct);
//
//        // Gọi method cần test
//        ResponseEntity<ProductDTO> response = controller.findById(1L);
//
//        // Assert kết quả
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody().getTitle()).isEqualTo("Test Product");
//    }
//
//    @Test
//    void whenProductExists() {
//        // Tạo đối tượng lớp con (VD: DVD)
//        DVD mockProduct = new DVD();
//        mockProduct.setId(1L);
//        mockProduct.setTitle("Inception");
//        mockProduct.setDirector("Christopher Nolan");
//        mockProduct.setStudio("Warner Bros");
//
//        // Mock service trả về đối tượng DVD
//        when(productService.findById(1L)).thenReturn(mockProduct);
//
//        // Gọi API
//        ResponseEntity<ProductDTO> response = controller.findById(1L);
//
//        // Assert
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isInstanceOf(ProductDTO.class);
//
//        ProductDTO responseBody = response.getBody();
//        assertThat(responseBody.getTitle()).isEqualTo("Inception");
//    }
//
//    @Test
//    void whenProductNotExists_thenReturn404() {
//        // mock service trả về null
//        when(productService.findById(99L)).thenReturn(null);
//
//        // Gọi controller
//        ResponseEntity<ProductDTO> response = controller.findById(99L);
//
//        // Assert status 404
//        assertThat(response.getStatusCodeValue()).isEqualTo(404);
//        assertThat(response.getBody()).isNull();
//    }
//}
//
