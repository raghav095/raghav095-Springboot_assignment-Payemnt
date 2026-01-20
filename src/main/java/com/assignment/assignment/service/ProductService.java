package com.assignment.assignment.service;

import com.assignment.assignment.model.Product;
import com.assignment.assignment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Service class for managing Product operations.
 * Handles creation, retrieval, updates, and stock management.
 */
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product updateProduct(String id, Product product) {
        if (productRepository.existsById(id)) {
            product.setId(id);
            return productRepository.save(product);
        }
        return null;
    }

    public boolean deleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean updateStock(String productId, Integer newStock) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setStock(newStock);
            productRepository.save(product);
            return true;
        }
        return false;
    }
}
