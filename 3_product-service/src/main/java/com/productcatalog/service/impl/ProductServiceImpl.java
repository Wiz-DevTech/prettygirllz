package com.productcatalog.service.impl;

import com.productcatalog.exception.InvalidSKUException;
import com.productcatalog.model.Product;
import com.productcatalog.repository.ProductRepository;
import com.productcatalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * # Implementation of Product service
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // SKU code pattern: 2 uppercase letters followed by 6 digits
    private static final Pattern SKU_PATTERN = Pattern.compile("[A-Z]{2}\\d{6}");

    @Override
    @Transactional
    public Product createProduct(Product product) {
        // Validate SKUs if any
        if (product.getSkus() != null) {
            product.getSkus().forEach(sku -> {
                if (!SKU_PATTERN.matcher(sku.getSkuCode()).matches()) {
                    throw new InvalidSKUException("Invalid SKU format: " + sku.getSkuCode());
                }
            });
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductWithSkusById(Long id) {
        return productRepository.findByIdWithSkus(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        // Check if product exists
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product not found with id: " + product.getId());
        }

        // Validate SKUs if any
        if (product.getSkus() != null) {
            product.getSkus().forEach(sku -> {
                if (!SKU_PATTERN.matcher(sku.getSkuCode()).matches()) {
                    throw new InvalidSKUException("Invalid SKU format: " + sku.getSkuCode());
                }
            });
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setActive(false);
            productRepository.save(product);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsWithAvailableInventory() {
        return productRepository.findProductsWithAvailableSkus();
    }
}