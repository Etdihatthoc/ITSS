package com.hust.ict.aims.service;
import com.hust.ict.aims.dto.CartRequestDTO;
import com.hust.ict.aims.dto.DeliveryInfoDTO;
import com.hust.ict.aims.model.RushOrder;

import java.util.List;

public interface RushOrderService {
    List<RushOrder> findAll();
    RushOrder findById(Long id);
    RushOrder save(RushOrder rushOrder);
    void deleteById(Long id);
    boolean isSupportedAddress(DeliveryInfoDTO deliveryInfoDTO);
    boolean isAnySupportedItem(CartRequestDTO cartRequestDTO);
}
