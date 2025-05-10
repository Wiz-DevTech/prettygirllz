package com.productcatalog.repository;

import com.productcatalog.model.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * # JPA repository for SKU entities
 */
@Repository
public interface SKURepository extends JpaRepository<SKU, Long> {

    /**
     * Find SKU by its unique code
     */
    Optional<SKU> findBySkuCode(String skuCode);

    /**
     * Find all SKUs for a product
     */
    List<SKU> findByProductId(Long productId);

    /**
     * Find all active SKUs for a product
     */
    List<SKU> findByProductIdAndActiveTrue(Long productId);

    /**
     * Find SKUs with available inventory
     */
    List<SKU> findByAvailableQuantityGreaterThanAndActiveTrue(Integer minQuantity);

    /**
     * Find a SKU with all its color variants eagerly loaded
     */
    @Query("SELECT s FROM SKU s LEFT JOIN FETCH s.colorVariants WHERE s.id = :id")
    Optional<SKU> findByIdWithColorVariants(@Param("id") Long id);

    /**
     * Find a SKU by code with all its color variants eagerly loaded
     */
    @Query("SELECT s FROM SKU s LEFT JOIN FETCH s.colorVariants WHERE s.skuCode = :skuCode")
    Optional<SKU> findBySkuCodeWithColorVariants(@Param("skuCode") String skuCode);
}