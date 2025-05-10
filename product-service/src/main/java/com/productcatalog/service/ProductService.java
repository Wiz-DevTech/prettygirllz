package com.productcatalog.service;

import com.productcatalog.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * # Business logic interface for Products
 */
public interface ProductService {

    /**
     * Create a new product
     */
    Product createProduct(Product product);

    /**
     * Get a product by ID
     */
    Optional<Product> getProductById(Long id);

    /**
     * Get a product with its SKUs by ID
     */
    Optional<Product> getProductWithSkusById(Long id);

    /**
     * Get all products
     */
    List<Product> getAllProducts();

    /**
     * Get products by category
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Search products by name
     */
    List<Product> searchProductsByName(String name);

    /**
     * Update a product
     */
    Product updateProduct(Product product);

    /**
     * Delete a product
     */
    void deleteProduct(Long id);

    /**
     * Deactivate a product (soft delete)
     */
    void deactivateProduct(Long id);

    /**
     * Get products with available inventory
     */
    List<Product> getProductsWithAvailableInventory();
}