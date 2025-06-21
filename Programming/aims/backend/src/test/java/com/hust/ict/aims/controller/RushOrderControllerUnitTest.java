//package com.hust.ict.aims.controller;
//
//import com.hust.ict.aims.model.*;
//import com.hust.ict.aims.service.RushOrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(RushOrderController.class)
//public class RushOrderControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private RushOrderService rushOrderService;
//
//    private RushOrder rushOrder;
//
//    @BeforeEach
//    public void setUp() {
//        rushOrder = new RushOrder();
//        rushOrder.setId(1L);
//        rushOrder.setDeliveryInstruction("House with a big white advertisement banner");
//        rushOrder.setDeliveryTime(LocalDateTime.of(2025, 5, 18, 6, 30, 0));
//
//        rushOrder.setTransaction(new Transaction());
//        rushOrder.setInvoice(new Invoice());
//        rushOrder.setDeliveryInfo(new DeliveryInfo());
//        rushOrder.setStatus("CREATED");
//    }
//
//    @Test
//    public void testGetRushOrders() throws Exception {
//        List<RushOrder> rushOrders = Collections.singletonList(rushOrder);
//        when(rushOrderService.findAll()).thenReturn(rushOrders);
//
//        mockMvc.perform(get("/api/rushorders"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].deliveryTime").value("2025-05-18T06:30:00"))
//                .andExpect(jsonPath("$[0].deliveryInstruction").value("House with a big white advertisement banner"));
//    }
//
//    @Test
//    public void testGetRushOrderById() throws Exception {
//        when(rushOrderService.findById(1L)).thenReturn(rushOrder);
//
//        mockMvc.perform(get("/api/rushorders/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.deliveryTime").value("2025-05-18T06:30:00"))
//                .andExpect(jsonPath("$.deliveryInstruction").value("House with a big white advertisement banner"));
//    }
//
//    @Test
//    public void testSaveRushOrder() throws Exception {
//        when(rushOrderService.save(any(RushOrder.class))).thenReturn(rushOrder);
//
//        String json = """
//            {
//                "id": 1,
//                "deliveryInstruction": "House with a big white advertisement banner",
//                "deliveryTime": "2025-05-18T06:30:00",
//                "status": "CREATED",
//                "transaction": {},
//                "invoice": {},
//                "deliveryInfo": {}
//            }
//            """;
//
//        mockMvc.perform(post("/api/rushorders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    public void testUpdateRushOrder() throws Exception {
//        RushOrder updatedRushOrder = new RushOrder();
//        updatedRushOrder.setId(1L);
//        updatedRushOrder.setDeliveryTime(LocalDateTime.of(2025, 5, 18, 12, 30, 0));
//        updatedRushOrder.setDeliveryInstruction("Updated delivery instruction");
//        updatedRushOrder.setTransaction(new Transaction());
//        updatedRushOrder.setInvoice(new Invoice());
//        updatedRushOrder.setDeliveryInfo(new DeliveryInfo());
//        updatedRushOrder.setStatus("COMPLETED");
//
//        when(rushOrderService.findById(1L)).thenReturn(rushOrder);
//        when(rushOrderService.save(any(RushOrder.class))).thenReturn(updatedRushOrder);
//
//        String json = """
//            {
//                "id": 1,
//                "deliveryInstruction": "Updated delivery instruction",
//                "deliveryTime": "2025-05-18T12:30:00",
//                "status": "UPDATED",
//                "transaction": {},
//                "invoice": {},
//                "deliveryInfo": {}
//            }
//            """;
//
//        mockMvc.perform(put("/api/rushorders/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.deliveryInstruction").value("Updated delivery instruction"))
//                .andExpect(jsonPath("$.deliveryTime").value("2025-05-18T12:30:00"));
//    }
//
//    @Test
//    public void testDeleteRushOrder() throws Exception {
//        when(rushOrderService.findById(1L)).thenReturn(rushOrder);
//        doNothing().when(rushOrderService).deleteById(1L);
//
//        mockMvc.perform(delete("/api/rushorders/1"))
//                .andExpect(status().isNoContent());
//
//        verify(rushOrderService, times(1)).deleteById(1L);
//    }
//}