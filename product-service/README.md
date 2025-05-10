# Product Catalog Service

This microservice handles product catalog operations.

## Development Steps

Here's the recommended order to develop this application:

1. **pom.xml** - Start by defining dependencies and build configuration
2. **application.yml & bootstrap.yml** - Set up application configuration 
3. **ProductCatalogApplication.java** - Create main application class
4. **JpaConfig.java** - Configure database connection
5. **FlywayConfig.java** - Set up database migration
6. **V1__Initial_schema.sql** - Create initial database schema
7. **V2__Add_constraints.sql** - Add database constraints
8. **Model classes** - Implement entity classes in this order:
   - Product.java
   - SKU.java
   - ColorVariant.java
9. **Repository interfaces** - Implement repository interfaces
10. **Service interfaces and implementations** - Implement business logic
11. **Exception classes** - Set up custom exceptions
12. **Controllers** - Implement REST API endpoints
13. **SwaggerConfig.java** - Configure API documentation
14. **Test classes** - Implement tests following the same flow
15. **Dockerfile** - Prepare containerization

Follow this order to ensure dependencies are properly set up before they're needed by dependent components.

