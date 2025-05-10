CREATE TABLE avatar_configs (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        avatar_type VARCHAR(50),
skin_tone VARCHAR(50),
hair_style VARCHAR(50),
    -- Add other columns based on your entity definition
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);