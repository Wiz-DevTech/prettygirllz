package com.wizdevtech;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wizdevtech.model.ApiResponse;
import com.wizdevtech.service.CacheService;

@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private CacheService cacheService; // Replace selfProxy with CacheService

    // Cacheable endpoint
    @GetMapping("/cache-test")
    public String cacheTest(@RequestParam String key) {
        // Delegate to the cache service
        return cacheService.getFromCache(key);
    }

    // Verification endpoint
    @GetMapping("/verify-cache")
    public String verifyCache(@RequestParam String key) {
        try {
            // Check if in cache
            String cachedResponse = cacheService.getFromCache(key);

            // Check database
            List<ApiResponse> dbEntries = cacheService.getFromDatabase(key);

            return String.format("""
                Cache Verification:
                - In-Memory Cache: %s
                - Database Entries: %d
                - Latest DB Entry: %s
                """,
                    cachedResponse != null ? "✅ Found" : "❌ Missing",
                    dbEntries.size(),
                    dbEntries.isEmpty() ? "N/A" : dbEntries.get(0).toString());
        } catch (Exception e) {
            log.error("Verification failed", e);
            return "Verification failed: " + e.getMessage();
        }
    }

    // Cleanup endpoint
    @DeleteMapping("/clear-test-data")
    public String clearTestData() {
        int deleted = cacheService.clearTestData();
        return "Cleared " + deleted + " test entries";
    }
}