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
 * API endpoints for Fashion products
 */
@RestController
@RequestMapping("/fashion")
@RequiredArgsConstructor
@Tag(name = "Fashion API", description = "Endpoints for fashion product management")
public class FashionController {

    private final ProductService productService;
    private static final String FASHION_CATEGORY = "FASHION";

    @GetMapping
    @Operation(summary = "Get all fashion products", description = "Retrieves a list of all fashion products")
    public ResponseEntity<List<Product>> getAllFashionProducts() {
        return ResponseEntity.ok(productService.getProductsByCategory(FASHION_CATEGORY));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fashion product by ID", description = "Retrieves a fashion product by its ID")
    public ResponseEntity<Product> getFashionProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .filter(product -> FASHION_CATEGORY.equals(product.getCategory()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new fashion product", description = "Creates a new fashion product")
    public ResponseEntity<Product> createFashionProduct(@RequestBody Product product) {
        product.setCategory(FASHION_CATEGORY);
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a fashion product", description = "Updates an existing fashion product")
    public ResponseEntity<Product> updateFashionProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.getProductById(id)
                .filter(existingProduct -> FASHION_CATEGORY.equals(existingProduct.getCategory()))
                .map(existingProduct -> {
                    product.setId(id);
                    product.setCategory(FASHION_CATEGORY);
                    return ResponseEntity.ok(productService.updateProduct(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a fashion product", description = "Deletes a fashion product by ID")
    public ResponseEntity<Void> deleteFashionProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id)
                .filter(product -> FASHION_CATEGORY.equals(product.getCategory()));

        if (productOptional.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}