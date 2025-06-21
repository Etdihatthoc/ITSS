//package com.hust.ict.aims.service;
//
//import com.hust.ict.aims.model.*;
//import com.hust.ict.aims.repository.RushOrderRepository;
//import com.hust.ict.aims.service.impl.RushOrderServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.willDoNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//public class RushOrderServiceUnitTest {
//    @Mock
//    private RushOrderRepository rushOrderRepository;
//
//    @InjectMocks
//    private RushOrderServiceImpl rushOrderService;
//
//    private RushOrder rushOrder;
//
//    private Logger logger;
//
//    @BeforeEach
//    public void setUp() {
//        logger = LoggerFactory.getLogger(RushOrderServiceUnitTest.class);
//
//        rushOrder = new RushOrder();
//        rushOrder.setId(1L);
//        rushOrder.setDeliveryInstruction("A house with a big advertisement banner");
//        rushOrder.setDeliveryTime(LocalDateTime.of(2025, 5, 18, 12, 30, 0));
//
//        rushOrder.setTransaction(new Transaction());
//        rushOrder.setInvoice(new Invoice());
//        rushOrder.setDeliveryInfo(new DeliveryInfo());
//        rushOrder.setStatus("CREATED");
//    }
//
//    @Test
//    @Order(1)
//    public void saveRushOrderTest() {
//        given(rushOrderRepository.save(rushOrder)).willReturn(rushOrder);
//
//        RushOrder savedRushOrder = rushOrderService.save(rushOrder);
//
//        logger.info(savedRushOrder.toString());
//        assertThat(savedRushOrder).isNotNull();
//        assertThat(savedRushOrder.getStatus()).isEqualTo("CREATED");
//    }
//
//    @Test
//    @Order(2)
//    public void getRushOrderByIdTest() {
//        given(rushOrderRepository.findById(1L)).willReturn(Optional.of(rushOrder));
//        RushOrder existingRushOrder = rushOrderService.findById(rushOrder.getId());
//        logger.info(existingRushOrder.toString());
//        assertThat(existingRushOrder).isNotNull();
//    }
//
//    @Test
//    @Order(3)
//    public void getAllRushOrdersTest() {
//        RushOrder rushOrder2 = new RushOrder();
//        rushOrder2.setId(2L);
//        rushOrder2.setDeliveryInstruction("A house with a big gate");
//        rushOrder2.setDeliveryTime(LocalDateTime.of(2025, 5, 18, 6, 30, 0));
//        rushOrder2.setTransaction(new Transaction());
//        rushOrder2.setInvoice(new Invoice());
//        rushOrder2.setDeliveryInfo(new DeliveryInfo());
//        rushOrder2.setStatus("COMPLETED");
//
//        given(rushOrderRepository.findAll()).willReturn(List.of(rushOrder, rushOrder2));
//
//        List<RushOrder> allRushOrders = rushOrderService.findAll();
//
//        logger.info(allRushOrders.toString());
//        assertThat(allRushOrders).isNotNull();
//        assertThat(allRushOrders.size()).isGreaterThan(1);
//    }
//
//    @Test
//    @Order(4)
//    public void updateRushOrderTest() {
//        given(rushOrderRepository.findById(1L)).willReturn(Optional.of(rushOrder));
//
//        rushOrder = rushOrderService.findById(1L);
//        rushOrder.setDeliveryInstruction("Nothing to specify");
//        rushOrder.setDeliveryTime(LocalDateTime.of(2025, 5, 20, 1, 2, 3));
//        rushOrder.setStatus("COMPLETED");
//
//        given(rushOrderRepository.save(rushOrder)).willReturn(rushOrder);
//        RushOrder updatedRushOrder = rushOrderService.save(rushOrder);
//
//        logger.info(updatedRushOrder.toString());
//        assertThat(updatedRushOrder).isNotNull();
//        assertThat(updatedRushOrder.getDeliveryInstruction()).isEqualTo("Nothing to specify");
//        assertThat(updatedRushOrder.getDeliveryTime()).isEqualTo(LocalDateTime.of(2025, 5, 20, 1, 2, 3));
//        assertThat(updatedRushOrder.getStatus()).isEqualTo("COMPLETED");
//    }
//
//    @Test
//    @Order(5)
//    public void deleteRushOrderTest() {
//        willDoNothing().given(rushOrderRepository).deleteById(1L);
//
//        rushOrderService.deleteById(1L);
//
//        verify(rushOrderRepository, times(1)).deleteById(1L);
//    }
//}