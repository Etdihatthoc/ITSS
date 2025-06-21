package com.hust.ict.aims.controller;

import com.hust.ict.aims.model.RushOrder;
import com.hust.ict.aims.service.RushOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rushorders")
@CrossOrigin(origins = "http://localhost:5173")
public class RushOrderController {
    private final RushOrderService rushOrderService;

    public RushOrderController(RushOrderService rushOrderService) {
        this.rushOrderService = rushOrderService;
    }

    @GetMapping()
    public List<RushOrder> getRushOrders() {
        return rushOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RushOrder> getRushOrder(@PathVariable Long id) {
        RushOrder rushOrder = rushOrderService.findById(id);
        if (rushOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rushOrder);
    }

    @PostMapping()
    public ResponseEntity<RushOrder> save(@RequestBody RushOrder rushOrder) {
        RushOrder savedRushOrder = rushOrderService.save(rushOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRushOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RushOrder> update(@PathVariable Long id, @RequestBody RushOrder rushOrder) {
        RushOrder existing = rushOrderService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setDeliveryInstruction(rushOrder.getDeliveryInstruction());
        existing.setDeliveryTime(rushOrder.getDeliveryTime());

        RushOrder updated = rushOrderService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        RushOrder existingRushOrder = rushOrderService.findById(id);
        if (existingRushOrder == null) {
            return ResponseEntity.notFound().build();
        }

        rushOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
