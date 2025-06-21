//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.dto.CartItemRequestDTO;
//import com.hust.ict.aims.model.Cart;
//import com.hust.ict.aims.model.CartItem;
//import com.hust.ict.aims.service.CartService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CartController.class)
//class CartControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private CartService cartService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testCreateCart() throws Exception {
//        Cart cart = new Cart();
//        cart.setId(1L);
//
//        when(cartService.createEmptyCart()).thenReturn(cart);
//
//        mockMvc.perform(post("/api/carts/create"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.cartId").value(1L));
//    }
//
//    @Test
//    void testAddItemToCart() throws Exception {
//        CartItemRequestDTO dto = new CartItemRequestDTO();
//        dto.setProductId(100L);
//        dto.setQuantity(2);
//
//        Cart cart = new Cart();
//        cart.setId(2L);
//        CartItem item = new CartItem();
//        item.setId(1L);
//        item.setQuantity(2);
//        cart.setItems(List.of(item));
//
//        when(cartService.addItemToCart(Mockito.eq(1L), any())).thenReturn(cart);
//
//        mockMvc.perform(post("/api/carts/1/items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.items[0].id").value(1L))
//                .andExpect(jsonPath("$.items[0].quantity").value(2));
//    }
//
//    @Test
//    void testGetCartById_Found() throws Exception {
//        Cart cart = new Cart();
//        cart.setId(2L);
//
//        when(cartService.findById(2L)).thenReturn(cart);
//
//        mockMvc.perform(get("/api/carts/2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.cartId").value(2L));
//    }
//
//    @Test
//    void testGetCartById_NotFound() throws Exception {
//        when(cartService.findById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/carts/999"))
//                .andExpect(status().isNotFound());
//    }
//
//
//    @Test
//    void testDeleteCart() throws Exception {
//        mockMvc.perform(delete("/api/carts/1"))
//                .andExpect(status().isOk());
//    }
//}
