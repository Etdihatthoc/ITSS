//package com.hust.ict.aims.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hust.ict.aims.model.Operation;
//import com.hust.ict.aims.service.OperationService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(OperationController.class)
//class OperationControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private OperationService operationService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testSaveOperation() throws Exception {
//        Operation operation = new Operation();
//        operation.setOperationID(1L);
//        operation.setOperationType("ADD_PRODUCT");
//        operation.setTimestamp(LocalDateTime.now());
//
//        when(operationService.save(any(Operation.class))).thenReturn(operation);
//
//        mockMvc.perform(post("/api/operations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(operation)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.operationID").value(1L))
//                .andExpect(jsonPath("$.operationType").value("ADD_PRODUCT"));
//    }
//
//    @Test
//    void testFindById() throws Exception {
//        Operation operation = new Operation();
//        operation.setOperationID(2L);
//        operation.setOperationType("DELETE_PRODUCT");
//        operation.setTimestamp(LocalDateTime.now());
//
//        when(operationService.findById(2L)).thenReturn(operation);
//
//        mockMvc.perform(get("/api/operations/2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.operationID").value(2L))
//                .andExpect(jsonPath("$.operationType").value("DELETE_PRODUCT"));
//    }
//
//    @Test
//    void testFindAll() throws Exception {
//        Operation op = new Operation();
//        op.setOperationID(3L);
//        op.setOperationType("UPDATE_PRODUCT");
//
//        when(operationService.findAll()).thenReturn(List.of(op));
//
//        mockMvc.perform(get("/api/operations"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].operationID").value(3L));
//    }
//
//    @Test
//    void testUpdateOperation() throws Exception {
//        Operation op = new Operation();
//        op.setOperationID(4L);
//        op.setOperationType("UPDATED");
//
//        when(operationService.update(eq(4L), any(Operation.class))).thenReturn(op);
//
//        mockMvc.perform(put("/api/operations/4")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(op)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.operationID").value(4L))
//                .andExpect(jsonPath("$.operationType").value("UPDATED"));
//    }
//
//    @Test
//    void testDeleteOperation() throws Exception {
//        doNothing().when(operationService).delete(5L);
//
//        mockMvc.perform(delete("/api/operations/5"))
//                .andExpect(status().isOk());
//    }
//}
