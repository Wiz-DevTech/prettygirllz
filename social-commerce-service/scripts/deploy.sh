#!/bin/bash
# Deployment script for Social Commerce Service

echo "Deploying Social Commerce Service..."

# Build the application
mvn clean package -DskipTests

# Run database migrations
flyway migrate

# Deploy to server
java -jar target/social-commerce-service.jar
