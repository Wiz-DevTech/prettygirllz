package com.productcatalog.controller;

import com.productcatalog.model.Product;
import com.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

/**
 * API endpoints for Candy products
 */
@RestController
@RequestMapping("/candy")
@RequiredArgsConstructor
@Tag(name = "Candy API", description = "Endpoints for candy product management")
public class CandyController {

    private final ProductService productService;
    private static final String CANDY_CATEGORY = "CANDY";

    @GetMapping
    @Operation(summary = "Get all candy products", description = "Retrieves a list of all candy products")
    public ResponseEntity<List<Product>> getAllCandyProducts() {
        return ResponseEntity.ok(productService.getProductsByCategory(CANDY_CATEGORY));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candy product by ID", description = "Retrieves a candy product by its ID")
    public ResponseEntity<Product> getCandyProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .filter(product -> CANDY_CATEGORY.equals(product.getCategory()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new candy product", description = "Creates a new candy product")
    public ResponseEntity<Product> createCandyProduct(@RequestBody Product product) {
        product.setCategory(CANDY_CATEGORY);
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a candy product", description = "Updates an existing candy product")
    public ResponseEntity<Product> updateCandyProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.getProductById(id)
                .filter(existingProduct -> CANDY_CATEGORY.equals(existingProduct.getCategory()))
                .map(existingProduct -> {
                    product.setId(id);
                    product.setCategory(CANDY_CATEGORY);
                    return ResponseEntity.ok(productService.updateProduct(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a candy product", description = "Deletes a candy product by ID")
    public ResponseEntity<Void> deleteCandyProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id)
                .filter(product -> CANDY_CATEGORY.equals(product.getCategory()));

        if (productOptional.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}