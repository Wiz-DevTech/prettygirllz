-- Create schema
CREATE SCHEMA IF NOT EXISTS identityaccess;

-- Drop tables if they exist to ensure clean schema
DROP TABLE IF EXISTS identityaccess.user_roles;
DROP TABLE IF EXISTS identityaccess.users;
DROP TABLE IF EXISTS identityaccess.roles;

-- Create users table
CREATE TABLE identityaccess.users (
                                      id BIGSERIAL PRIMARY KEY,
                                      email VARCHAR(255) NOT NULL UNIQUE,
                                      password VARCHAR(255) NOT NULL,
                                      enabled BOOLEAN DEFAULT TRUE,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create roles table
CREATE TABLE identityaccess.roles (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL UNIQUE
);

-- Create join table for users and roles (many-to-many)
CREATE TABLE identityaccess.user_roles (
                                           user_id BIGINT NOT NULL,
                                           role_id BIGINT NOT NULL,
                                           PRIMARY KEY (user_id, role_id),
                                           FOREIGN KEY (user_id) REFERENCES identityaccess.users(id) ON DELETE CASCADE,
                                           FOREIGN KEY (role_id) REFERENCES identityaccess.roles(id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO identityaccess.roles (name) VALUES ('ROLE_USER');
INSERT INTO identityaccess.roles (name) VALUES ('ROLE_ADMIN');