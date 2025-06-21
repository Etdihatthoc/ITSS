//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.model.DeliveryInfo;
//import com.hust.ict.aims.service.DeliveryInfoService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(DeliveryInfoController.class)
//class DeliveryInfoControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private DeliveryInfoService deliveryInfoService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testSaveDeliveryInfo() throws Exception {
//        DeliveryInfo info = new DeliveryInfo();
//        info.setId(1L);
//        info.setRecipientName("John Doe");
//
//        when(deliveryInfoService.save(any(DeliveryInfo.class))).thenReturn(info);
//
//        mockMvc.perform(post("/api/delivery-infos")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(info)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.recipientName").value("John Doe"));
//    }
//
//    @Test
//    void testFindAllDeliveryInfos() throws Exception {
//        DeliveryInfo info = new DeliveryInfo();
//        info.setId(1L);
//        info.setRecipientName("John Doe");
//
//        when(deliveryInfoService.findAll()).thenReturn(List.of(info));
//
//        mockMvc.perform(get("/api/delivery-infos"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].recipientName").value("John Doe"));
//    }
//
//    @Test
//    void testFindDeliveryInfoById_Found() throws Exception {
//        DeliveryInfo info = new DeliveryInfo();
//        info.setId(1L);
//        info.setRecipientName("Jane Doe");
//
//        when(deliveryInfoService.findById(1L)).thenReturn(info);
//
//        mockMvc.perform(get("/api/delivery-infos/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.recipientName").value("Jane Doe"));
//    }
//
//    @Test
//    void testFindDeliveryInfoById_NotFound() throws Exception {
//        when(deliveryInfoService.findById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/delivery-infos/999"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("")); // Controller returns null, resulting in 200 with empty body
//    }
//
//    @Test
//    void testUpdateDeliveryInfo() throws Exception {
//        DeliveryInfo updated = new DeliveryInfo();
//        updated.setId(1L);
//        updated.setRecipientName("Updated Name");
//
//        when(deliveryInfoService.save(any(DeliveryInfo.class))).thenReturn(updated);
//
//        mockMvc.perform(put("/api/delivery-infos/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updated)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.recipientName").value("Updated Name"));
//    }
//
//    @Test
//    void testDeleteDeliveryInfo() throws Exception {
//        doNothing().when(deliveryInfoService).deleteById(1L);
//
//        mockMvc.perform(delete("/api/delivery-infos/1"))
//                .andExpect(status().isOk());
//    }
//}
