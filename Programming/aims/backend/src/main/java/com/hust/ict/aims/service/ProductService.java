package com.hust.ict.aims.service;

import java.util.List;
import java.util.Optional;

import com.hust.ict.aims.dto.ProductDTO;
import com.hust.ict.aims.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

/* Cohesion Level: Functional Cohesion
 * Exhibits functional cohesion as all members contribute to the single responsibility of representing a product. The class manages product attributes like ID, title, price, and quantity, with all methods directly related to these attributes.
*/

@Service
public interface ProductService {
    Product save(Product product);
    Product findById(Long id);
    List<Product> findAll();
    Page<ProductDTO> findRandom(int page, int size);
    Product update(Long id, Product product);
    void softDelete(Long id);
    void hardDelete(Long id); // For admin use only
}
