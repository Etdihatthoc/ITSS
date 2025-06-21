package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.DeliveryInfoDTO;
import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.service.DeliveryInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-infos")
@CrossOrigin(origins = "http://localhost:5173")
public class DeliveryInfoController {

    private final DeliveryInfoService deliveryInfoService;

    public DeliveryInfoController(DeliveryInfoService deliveryInfoService) {
        this.deliveryInfoService = deliveryInfoService;
    }

    @PostMapping
    public ResponseEntity<DeliveryInfo> save(@RequestBody DeliveryInfo deliveryInfo) {
        DeliveryInfo saved = deliveryInfoService.save(deliveryInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<DeliveryInfo> findAll() {
        return deliveryInfoService.findAll();
    }

    @GetMapping("/{id}")
    public DeliveryInfo findById(@PathVariable Long id) {
        return deliveryInfoService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryInfo> update(@PathVariable Long id, @RequestBody DeliveryInfoDTO deliveryInfo) {
        DeliveryInfo existing = deliveryInfoService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setDeliveryAddress(deliveryInfo.getDeliveryAddress());
        existing.setProvince(deliveryInfo.getProvince());
        existing.setPhoneNumber(deliveryInfo.getPhoneNumber());
        existing.setRecipientName(deliveryInfo.getRecipientName());
        existing.setEmail(deliveryInfo.getEmail());

        DeliveryInfo updated = deliveryInfoService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deliveryInfoService.deleteById(id);
    }
}
