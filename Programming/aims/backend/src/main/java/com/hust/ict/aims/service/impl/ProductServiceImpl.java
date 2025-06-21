package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.ProductDTO;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    private ProductRepository repo;

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
        return productRepository.findAll();
    }

    @Override
    public Page<ProductDTO> findRandom(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllRandom(pageable)
                .map(ProductDTO::fromEntity);
    }

    @Override
    public Product update(Long id, Product product) {
        if (!productRepository.existsById(id)) return null;
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
