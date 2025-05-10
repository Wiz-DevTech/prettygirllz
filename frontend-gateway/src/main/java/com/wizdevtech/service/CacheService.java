package com.wizdevtech.service;


import com.wizdevtech.model.ApiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Cacheable(value = "api_responses", key = "#key")
    public String getFromCache(String key) {
        String response = "Generated response for: " + key + " | Time: " + LocalDateTime.now();
        log.info("❗ Cache miss - Generating new response for key: {}", key);
        return response;
    }

    public List<ApiResponse> getFromDatabase(String key) {
        return entityManager.createQuery(
                        "SELECT a FROM ApiResponse a WHERE a.key = :key", ApiResponse.class)
                .setParameter("key", key)
                .getResultList();
    }

    public int clearTestData() {
        int deleted = entityManager.createQuery(
                        "DELETE FROM ApiResponse WHERE key LIKE 'test-%'")
                .executeUpdate();
        log.info("🧹 Deleted {} test entries", deleted);
        return deleted;
    }
}