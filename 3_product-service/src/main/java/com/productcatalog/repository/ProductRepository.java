package com.productcatalog.repository;

import com.productcatalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * # JPA repository for Product entities
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);

    /**
     * Find active products by category with pagination
     */
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    /**
     * Find products with name containing the search term (case insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find a product with all its SKUs eagerly loaded
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.skus WHERE p.id = :id")
    Optional<Product> findByIdWithSkus(@Param("id") Long id);

    /**
     * Find products with available SKUs
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.skus s WHERE s.availableQuantity > 0 AND p.active = true")
    List<Product> findProductsWithAvailableSkus();
}