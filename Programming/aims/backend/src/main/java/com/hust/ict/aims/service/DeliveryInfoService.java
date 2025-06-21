package com.hust.ict.aims.service;

import com.hust.ict.aims.dto.DeliveryInfoDTO;
import com.hust.ict.aims.model.DeliveryInfo;

import java.util.List;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All methods are tightly focused on managing DeliveryInfo persistence.
// No unrelated logic such as shipping policies, validation rules, or controller logic.
//
// SRP COMMENT:
// Single Responsibility: This service is solely responsible for CRUD operations on DeliveryInfo.
// It does not handle order processing, logistics, or business rules beyond repository delegation.
// ------------------------------------------------------------

public interface DeliveryInfoService {
    List<com.hust.ict.aims.model.DeliveryInfo> findAll();
    com.hust.ict.aims.model.DeliveryInfo findById(Long id);
    DeliveryInfo save(DeliveryInfo deliveryInfo);
    void deleteById(Long id);
}
