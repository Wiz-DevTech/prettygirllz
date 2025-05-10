package com.productcatalog.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * # Configuration for Swagger/OpenAPI documentation
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API")
                        .description("API for managing product catalog")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://example.com/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("Product Catalog Documentation")
                        .url("https://example.com/docs"));
    }
}