package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.ProductDTO;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Cohesion Level: Functional Cohesion
 * This class exhibits functional cohesion because all its methods focus on a single responsibility:
 * providing business logic and CRUD operations for Product entities.
 *
 * SRP: Compliant
 * The class does not mix unrelated logic; it delegates persistence to the repository and
 * contains service-level behavior such as unproxying Product instances for correct subclass access.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @PersistenceContext
    public EntityManager entityManager;

    @Override
    public Product findById(Long id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            // Unproxy để lấy đối tượng thực sự
            if (product instanceof HibernateProxy) {
                product = (Product) ((HibernateProxy) product).getHibernateLazyInitializer().getImplementation();
            }

            Hibernate.initialize(product);
        }
        return product;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAllActive();
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public Page<Product> getProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
        //return productRepository.findAllActiveRandom(PageRequest.of(page, size));
        //return productRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
    }

    @Override
    public Product update(Long id, Product product) {
        if (!productRepository.existsById(id)) return null;
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    public void softDelete(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public void hardDelete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProductsWithFilters(String search, String category, String productType, 
                                                 Double minPrice, Double maxPrice, String sortBy, 
                                                 String sortDirection, int page, int size) {
        
        // Create sort specification
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Build dynamic query using Specification pattern
        Specification<Product> spec = Specification.where(null);
        
        // Search by title (case-insensitive)
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), 
                                   "%" + search.toLowerCase() + "%"));
        }
        
        // Filter by category
        if (category != null && !category.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("category"), category));
        }
        
        // Filter by product type
        if (productType != null && !productType.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("productType"), productType));
        }
        
        // Filter by price range
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), maxPrice));
        }
        
        // Only show non-deleted products
        spec = spec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("deleted"), false));
        
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public List<Product> getRandomPage(int size) {
        // Get min and max IDs for active products
        Long minId = productRepository.findMinId();
        Long maxId = productRepository.findMaxId();
        
        // Handle empty database case
        if (minId == null || maxId == null || minId > maxId) {
            return Collections.emptyList();
        }
        
        // Generate random pivot between min and max ID
        long pivot = ThreadLocalRandom.current().nextLong(minId, maxId + 1);
        
        // Get products from pivot onwards
        List<Product> first = productRepository.findFromPivot(pivot);
        
        // Apply size limit to first list
        if (first.size() > size) {
            first = first.subList(0, size);
        }
        
        // If we don't have enough products, wrap around to the beginning
        if (first.size() < size) {
            List<Product> wrap = productRepository.findWrapAround(pivot);
            
            // Apply remaining size limit to wrap list
            int remainingSize = size - first.size();
            if (wrap.size() > remainingSize) {
                wrap = wrap.subList(0, remainingSize);
            }
            
            first.addAll(wrap);
        }
        
        return first;
    }

    @Override
    public Page<Product> getRandomProducts(int page, int size) {
        // Get total count of active products
        long totalElements = productRepository.countActiveProducts();
        
        if (totalElements == 0) {
            return Page.empty();
        }
        
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Ensure page is within bounds
        if (page >= totalPages) {
            page = totalPages - 1;
        }
        if (page < 0) {
            page = 0;
        }
        
        // Get all active products ordered by ID
        List<Product> allProducts = productRepository.findAllActiveOrdered();
        
        // Generate a seed based on current minute (changes every minute)
        // This ensures same order for same minute, different order each minute
        int seed = (int) (System.currentTimeMillis() / (1000 * 60)); // Minute-based seed
        
        // Shuffle the list using the seed for consistent random order
        Collections.shuffle(allProducts, new java.util.Random(seed));
        
        // Apply pagination manually
        int start = page * size;
        int end = Math.min(start + size, allProducts.size());
        
        List<Product> content = start < allProducts.size() ? 
            allProducts.subList(start, end) : Collections.emptyList();
        
        // Create Page object
        return new PageImpl<>(content, org.springframework.data.domain.PageRequest.of(page, size), totalElements);
    }

    @Override
    @Transactional
    public Product updateProductStock(Long productId, int quantity, String operation) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        int oldQuantity = product.getQuantity();
        int newQuantity;

        if ("increase".equalsIgnoreCase(operation)) {
            newQuantity = oldQuantity + quantity;
        } else if ("decrease".equalsIgnoreCase(operation)) {
            newQuantity = oldQuantity - quantity;

            // Prevent negative stock
            if (newQuantity < 0) {
                throw new IllegalArgumentException(
                        "Cannot decrease stock below zero. Current stock: " +
                                oldQuantity + ", Requested decrease: " + quantity
                );
            }
        } else {
            throw new IllegalArgumentException(
                    "Invalid operation: " + operation + ". Must be 'increase' or 'decrease'"
            );
        }

        product.setQuantity(newQuantity);

        return productRepository.save(product);
    }
}