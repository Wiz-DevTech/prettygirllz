package com.productcatalog.service.impl;

import com.productcatalog.model.SKU;
import com.productcatalog.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * # Implementation of Inventory service
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl {

    private final SKURepository skuRepository;

    /**
     * Check if a SKU is in stock
     */
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode) {
        Optional<SKU> sku = skuRepository.findBySkuCode(skuCode);
        return sku.map(s -> s.isActive() && s.getAvailableQuantity() > 0).orElse(false);
    }

    /**
     * Add inventory for a SKU
     */
    @Transactional
    public SKU addInventory(String skuCode, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        SKU sku = skuRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("SKU not found: " + skuCode));

        sku.setAvailableQuantity(sku.getAvailableQuantity() + quantity);
        return skuRepository.save(sku);
    }

    /**
     * Remove inventory for a SKU (for order fulfillment)
     */
    @Transactional
    public SKU removeInventory(String skuCode, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        SKU sku = skuRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("SKU not found: " + skuCode));

        if (sku.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory for SKU: " + skuCode);
        }

        sku.setAvailableQuantity(sku.getAvailableQuantity() - quantity);
        return skuRepository.save(sku);
    }

    /**
     * Update inventory for a SKU (set exact quantity)
     */
    @Transactional
    public SKU updateInventory(String skuCode, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        SKU sku = skuRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("SKU not found: " + skuCode));

        sku.setAvailableQuantity(newQuantity);
        return skuRepository.save(sku);
    }
}