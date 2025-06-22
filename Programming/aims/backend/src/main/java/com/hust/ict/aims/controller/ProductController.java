package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.*;
import com.hust.ict.aims.exception.ProductNotFoundException;
import com.hust.ict.aims.model.*;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.OperationService;
import com.hust.ict.aims.service.ProductService;
import com.hust.ict.aims.service.ProductValidationService;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hust.ict.aims.service.BusinessRuleValidator;
import com.hust.ict.aims.factory.ProductFactory;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;
    private final OperationService operationService;
    private final BusinessRuleValidator businessRuleValidator; // Change to final
    private final ProductFactory productFactory; // Change to final

    public ProductController(ProductService productService,
                             OperationService operationService,
                             BusinessRuleValidator businessRuleValidator,
                             ProductFactory productFactory) {
        this.productService = productService;
        this.operationService = operationService;
        this.businessRuleValidator = businessRuleValidator;
        this.productFactory = productFactory;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Product product) {
        try {
            // Check daily limits for adds
            if (!businessRuleValidator.canExecuteOperation(null, "ADD_PRODUCT")) {
                return ResponseEntity.badRequest()
                        .body(businessRuleValidator.getFailureReason("ADD_PRODUCT"));
            }

            validateProductInformation(product);
            Product saved = productService.save(product);

            Operation op = new Operation();
            op.setProduct(saved);
            op.setOperationType("ADD_PRODUCT");
            op.setTimestamp(LocalDateTime.now());
            operationService.save(op);

            return ResponseEntity.ok(saved);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating product: " + e.getMessage());
        }
    }

    // Add new endpoint for creating products with factory
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request) {
        try {
            if (!businessRuleValidator.canExecuteOperation(null, "ADD_PRODUCT")) {
                return ResponseEntity.badRequest()
                        .body(businessRuleValidator.getFailureReason("ADD_PRODUCT"));
            }

            Product product = productFactory.createProduct(request);
            validateProductInformation(product);
            Product saved = productService.save(product);

            Operation op = new Operation();
            op.setProduct(saved);
            op.setOperationType("ADD_PRODUCT");
            op.setTimestamp(LocalDateTime.now());
            operationService.save(op);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating product: " + e.getMessage());
        }
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        try {
            // Check if product exists
            Product existingProduct = productService.findById(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            // Check daily limits for updates
            if (!businessRuleValidator.canExecuteOperation(id, "UPDATE_PRODUCT")) {
                return ResponseEntity.badRequest()
                        .body(businessRuleValidator.getFailureReason("UPDATE_PRODUCT"));
            }

            validateProductInformation(product);

            // Check price update rules
            if (existingProduct.getCurrentPrice() != product.getCurrentPrice()) {
                validatePriceRange(product.getValue(), product.getCurrentPrice());
                validatePriceUpdateFrequency(id);
            }

            product.setId(id);
            Product updated = productService.save(product);

            Operation op = new Operation();
            op.setProduct(updated);
            op.setOperationType("UPDATE_PRODUCT");
            op.setTimestamp(LocalDateTime.now());
            operationService.save(op);

            return ResponseEntity.ok(updated);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating product: " + e.getMessage());
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
    public void delete(@PathVariable Long id) {
        productService.delete(id);
        Operation op = new Operation();
        Product dummy = new Product();
        dummy.setId(id);
        op.setProduct(dummy);
        op.setOperationType("DELETE_PRODUCT");
        op.setTimestamp(LocalDateTime.now());
        operationService.save(op);
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
