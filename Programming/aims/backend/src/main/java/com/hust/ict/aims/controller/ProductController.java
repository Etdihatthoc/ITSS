package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.*;
import com.hust.ict.aims.exception.ProductNotFoundException;
import com.hust.ict.aims.model.*;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.repository.OperationRepository;
import com.hust.ict.aims.service.AddProductOperation;
import com.hust.ict.aims.service.BusinessRulesService;
import com.hust.ict.aims.service.OperationService;
import com.hust.ict.aims.service.ProductService;
import com.hust.ict.aims.service.UpdateProductOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    
    @Autowired
    private AddProductOperation addProductOperation;

    @Autowired
    private UpdateProductOperation updateProductOperation;

    @Autowired
    private BusinessRulesService businessRulesService;
    private final ProductService productService;
    private final OperationService operationService;

    public ProductController(ProductService productService,
                             OperationService operationService) {
        this.productService = productService;
        this.operationService = operationService;
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        return addProductOperation.executeOperation(product, "ADD_PRODUCT");
    }

    // Phương thức kiểm tra tính hợp lệ của thông tin sản phẩm
    private void validateProductInformation(Product product) {
        // Kiểm tra title
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product title must not be empty"
            );
        }

        // Kiểm tra các trường bắt buộc khác
        if (product.getImageURL() == null || product.getImageURL().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product image URL must not be empty"
            );
        }

        if (product.getProductDimensions() == null || product.getProductDimensions().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product dimensions must not be empty"
            );
        }

        if (product.getWarehouseEntryDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warehouse entry date must not be empty"
            );
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product category must not be empty"
            );
        }

        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product barcode must not be empty"
            );
        }

        if (product.getValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product value must be greater than zero"
            );
        }

        if (product.getCurrentPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product price must be greater than zero"
            );
        }

        if (product.getWeight() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product weight must be greater than zero"
            );
        }

        if (product.getQuantity() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product quantity cannot be negative"
            );
        }
    }

    @GetMapping("/operations")
    public ResponseEntity<Map<String, Object>> getProductOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String userId) {
        
        try {
            // Use the service layer instead of repository directly
            Page<Operation> operationsPage = operationService.findOperationsWithFilters(
                    search, operationType, page, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", operationsPage.getContent());
            response.put("total", operationsPage.getTotalElements());
            response.put("page", page);
            response.put("totalPages", operationsPage.getTotalPages());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to fetch operations: " + e.getMessage()));
        }
    }

    @GetMapping("/{productId}/operations")
    public ResponseEntity<Map<String, Object>> getProductOperationHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // Use service layer
            List<Operation> allProductOperations = operationService.findByProductId(productId);
            
            // Apply pagination manually (or you can create a paginated version in service)
            int start = page * limit;
            int end = Math.min(start + limit, allProductOperations.size());
            List<Operation> paginatedOperations = start < allProductOperations.size() ? 
                allProductOperations.subList(start, end) : Collections.emptyList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", paginatedOperations);
            response.put("total", allProductOperations.size());
            response.put("page", page);
            response.put("totalPages", (int) Math.ceil((double) allProductOperations.size() / limit));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to fetch product operations: " + e.getMessage()));
        }
    } 
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProductDTO.fromEntity(product)); // Sử dụng static method
    }

    @GetMapping
    public List<ProductDTO> findAll() {
        return productService.findAll().stream()
                .map(ProductDTO::fromEntity) // Ánh xạ từng entity sang DTO
                .collect(Collectors.toList());
    }

//    @GetMapping("/{page}")
//    public Page<ProductDTO> list(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        return productService.findRandom(page, size);
//    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return updateProductOperation.setProductId(id).executeOperation(product, "UPDATE_PRODUCT");
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<String> deleteBulk(@RequestBody List<Long> productIds) {
        if (!businessRulesService.canPerformBulkDelete(productIds.size())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot delete more than 10 products at once"
            );
        }
        
        try {
            businessRulesService.acquireOperationLock("DELETE_PRODUCT");
            businessRulesService.validateDailyOperationLimits("DELETE_PRODUCT", LocalDateTime.now());
            
            for (Long id : productIds) {
                Product product = productService.findById(id);
                if (product != null) {
                    productService.softDelete(id);
                    
                    // Log each deletion
                    Operation op = new Operation();
                    op.setProduct(product);
                    op.setOperationType("DELETE_PRODUCT");
                    op.setTimestamp(LocalDateTime.now());
                    operationService.save(op);
                }
            }
            
            return ResponseEntity.ok("Successfully deleted " + productIds.size() + " products");
            
        } finally {
            businessRulesService.releaseOperationLock("DELETE_PRODUCT");
        }
    }
    // Kiểm tra giá mới có nằm trong khoảng 30%-150% của giá trị không
    private void validatePriceRange(float productValue, float newPrice) {
        float minPrice = productValue * 0.3f;  // 30% của giá trị
        float maxPrice = productValue * 1.5f;  // 150% của giá trị

        if (newPrice < minPrice || newPrice > maxPrice) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New price must be between 30% and 150% of the product value"
            );
        }
    }

    // Kiểm tra số lần cập nhật giá trong ngày
    private void validatePriceUpdateFrequency(Long productId) {
        // Lấy ngày hiện tại
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // Đếm số lần cập nhật giá trong ngày
        long updateCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                productId, "UPDATE_PRODUCT", startOfDay, endOfDay);

        if (updateCount >= 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot update product price more than 2 times per day"
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            businessRulesService.acquireOperationLock("DELETE_PRODUCT");
            businessRulesService.validateDailyOperationLimits("DELETE_PRODUCT", LocalDateTime.now());
            
            Product product = productService.findById(id);
            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product with ID " + id + " not found"
                );
            }
            
            // Soft delete the product
            productService.softDelete(id);
            
            // Log the operation
            Operation op = new Operation();
            op.setProduct(product); // Use the existing product object
            op.setOperationType("DELETE_PRODUCT");
            op.setTimestamp(LocalDateTime.now());
            operationService.save(op);
            
            return ResponseEntity.ok("Product deleted successfully");
            
        } finally {
            businessRulesService.releaseOperationLock("DELETE_PRODUCT");
        }
    }

    @PostMapping("/check-inventory")
    public ResponseEntity<?> checkInventory(@RequestBody CartItemsRequest request) {
        List<OutOfStockProduct> outOfStockProducts = new ArrayList<>();

        for (CartItemRequest item : request.getItems()) {
            Product product = productService.findById(item.getProductId());

            if (product == null) {
                throw new ProductNotFoundException(item.getProductId());
            }

            if (product.getQuantity() < item.getQuantity()) {
                outOfStockProducts.add(new OutOfStockProduct(
                        product.getId(),
                        item.getQuantity(),
                        product.getQuantity(),
                        product.getTitle(),
                        "Insufficient inventory. Only " + product.getQuantity() + " available."
                ));
            }
        }

        if (outOfStockProducts.isEmpty()) {
            return ResponseEntity.ok(new InventoryCheckResponse(true, null));
        } else {
            return ResponseEntity.badRequest().body(
                    new InventoryCheckResponse(false, outOfStockProducts)
            );
        }
    }
}
