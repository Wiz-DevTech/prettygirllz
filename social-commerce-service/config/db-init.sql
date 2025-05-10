-- Database Initialization Script
CREATE DATABASE social_commerce;
CREATE USER social_commerce_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE social_commerce TO social_commerce_user;

-- Create schemas if needed
CREATE SCHEMA IF NOT EXISTS social_commerce;
