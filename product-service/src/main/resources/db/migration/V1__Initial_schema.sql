-- # Flyway migration: Initial database schema
-- Creates the basic tables structure

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          category VARCHAR(50) NOT NULL,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(50),
                          last_modified_by VARCHAR(50),
                          active BOOLEAN DEFAULT TRUE
);

CREATE TABLE skus (
                      id BIGSERIAL PRIMARY KEY,
                      sku_code VARCHAR(10) NOT NULL UNIQUE,
                      product_id BIGINT NOT NULL,
                      price DECIMAL(10, 2) NOT NULL,
                      available_quantity INT NOT NULL DEFAULT 0,
                      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      created_by VARCHAR(50),
                      last_modified_by VARCHAR(50),
                      active BOOLEAN DEFAULT TRUE
);

CREATE TABLE color_variants (
                                id BIGSERIAL PRIMARY KEY,
                                sku_id BIGINT NOT NULL,
                                color_name VARCHAR(50) NOT NULL,
                                color_code VARCHAR(10),
                                image_url VARCHAR(255),
                                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                created_by VARCHAR(50),
                                last_modified_by VARCHAR(50)
);

-- Create indexes for performance
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_skus_product_id ON skus(product_id);
CREATE INDEX idx_color_variants_sku_id ON color_variants(sku_id);