package com.hust.ict.aims.controller;

import com.hust.ict.aims.model.Operation;
import com.hust.ict.aims.service.OperationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "http://localhost:5173")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    /**
     * Tạo mới một Operation
     * POST /api/operations
     */
    @PostMapping
    public Operation save(@RequestBody Operation operation) {
        return operationService.save(operation);
    }

    /**
     * Lấy chi tiết Operation theo ID
     * GET /api/operations/{id}
     */
    @GetMapping("/{id}")
    public Operation findById(@PathVariable Long id) {
        return operationService.findById(id);
    }

    /**
     * Lấy danh sách tất cả Operation
     * GET /api/operations
     */
    @GetMapping
    public List<Operation> findAll() {
        return operationService.findAll();
    }

    /**
     * Cập nhật Operation theo ID
     * PUT /api/operations/{id}
     */
    @PutMapping("/{id}")
    public Operation update(@PathVariable Long id, @RequestBody Operation operation) {
        return operationService.update(id, operation);
    }

    /**
     * Xóa Operation theo ID
     * DELETE /api/operations/{id}
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        operationService.delete(id);
    }
}
