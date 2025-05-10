CREATE TABLE logistics.deliveries (
                                      id SERIAL PRIMARY KEY,
                                      order_id VARCHAR(50) NOT NULL,
                                      destination_type VARCHAR(20) NOT NULL,
                                      status VARCHAR(20) NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE logistics.fraud_blacklist (
                                           id SERIAL PRIMARY KEY,
                                           phone VARCHAR(20) NOT NULL UNIQUE,
                                           reason VARCHAR(255),
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);