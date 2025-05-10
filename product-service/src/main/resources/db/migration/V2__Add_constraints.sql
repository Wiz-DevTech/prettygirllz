-- # Flyway migration: Add constraints
-- Adds foreign keys and other constraints

-- Add foreign key constraints
ALTER TABLE skus
    ADD CONSTRAINT fk_skus_product_id
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE;

ALTER TABLE color_variants
    ADD CONSTRAINT fk_color_variants_sku_id
        FOREIGN KEY (sku_id)
            REFERENCES skus (id)
            ON DELETE CASCADE;

-- Add check constraints
ALTER TABLE skus
    ADD CONSTRAINT chk_skus_price_positive
        CHECK (price > 0);

ALTER TABLE skus
    ADD CONSTRAINT chk_skus_available_quantity_nonnegative
        CHECK (available_quantity >= 0);

-- Add pattern constraint for SKU code (will be enforced in application)
COMMENT ON COLUMN skus.sku_code IS 'Format: [A-Z]{2}[0-9]{6}';