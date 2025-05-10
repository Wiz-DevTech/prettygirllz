CREATE TABLE avatar_configs (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT UNIQUE REFERENCES users(id),
                                avatar_type VARCHAR(20) NOT NULL,
                                avatar_preview_url VARCHAR(255),
                                version INTEGER NOT NULL DEFAULT 1,
                                face_shape VARCHAR(50),
                                skin_tone VARCHAR(50),
                                hair_style VARCHAR(50),
                                hair_color VARCHAR(50),
                                eye_color VARCHAR(50),
                                body_type VARCHAR(50),
                                height DOUBLE PRECISION,
                                clothing_style VARCHAR(50),
                                animation_driver VARCHAR(20),
                                rendering_engine VARCHAR(20),
                                additional_settings JSONB,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_avatar_configs_user_id ON avatar_configs(user_id);