package com.wizdevtech.controller;

import com.wizdevtech.model.ApiResponse;
import com.wizdevtech.model.SsrCache;
import java.util.Optional;
import java.time.Instant;
import java.util.logging.Logger;

import com.wizdevtech.repository.ApiResponseRepository;
import com.wizdevtech.repository.SsrCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());

    @Autowired
    private ApiResponseRepository apiResponseRepository;

    @Autowired
    private SsrCacheRepository ssrCacheRepository;

    // Health check - unchanged
    @GetMapping("/test")
    public String test() {
        logger.info("Test endpoint called");
        return "API is working! Current time: " + Instant.now();
    }

    // Original product endpoint - unchanged
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        logger.info("Product endpoint called for id: " + id);
        try {
            Optional<ApiResponse> cached = findInCache(id);
            if (cached.isPresent()) {
                logger.info("Cache hit for product id: " + id);
                return ResponseEntity.ok(cached.get().getResponseJson());
            }

            logger.info("Cache miss for product id: " + id);
            String mockResponse = "{\"name\":\"Sample Product\"}";
            saveToCache(id, mockResponse);
            return ResponseEntity.ok(mockResponse);

        } catch (Exception e) {
            logger.severe("Error in product endpoint: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // New SSR fallback endpoint
    @GetMapping("/fallback/{route}")
    public ResponseEntity<String> getSsrFallback(@PathVariable String route) {
        logger.info("Fallback endpoint called for route: " + route);
        try {
            // Normalize route to ensure leading slash
            String normalizedRoute = route.startsWith("/") ? route : "/" + route;
            logger.info("Normalized route: " + normalizedRoute);

            Optional<SsrCache> cached = ssrCacheRepository.findByRouteAndExpiryAfter(
                    normalizedRoute,
                    Instant.now()
            );

            if (cached.isPresent()) {
                logger.info("Cache hit for SSR route: " + normalizedRoute);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(cached.get().getHtml());
            }

            logger.info("Cache miss for SSR route: " + normalizedRoute);
            return ResponseEntity.status(404)
                    .body("<div>No cached version</div>");

        } catch (Exception e) {
            logger.severe("Error in fallback endpoint: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body("<div>Server Error</div>");
        }
    }

    @Transactional(readOnly = true)
    protected Optional<ApiResponse> findInCache(String id) {
        logger.info("Looking up product in cache: " + id);
        return apiResponseRepository.findById(id);  // Breakpoint 3
    }

    @Transactional
    protected void saveToCache(String id, String response) {
        logger.info("Saving product to cache: " + id);
        apiResponseRepository.deleteById(id);
        apiResponseRepository.save(
                new ApiResponse(
                        id,
                        "product-api",
                        response,
                        Instant.now().plusSeconds(3600)
                )
        );
    }
}