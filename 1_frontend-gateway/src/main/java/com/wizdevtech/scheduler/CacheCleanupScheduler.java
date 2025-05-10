package com.wizdevtech.scheduler;


import com.wizdevtech.repository.ApiResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Component
public class CacheCleanupScheduler {

    @Autowired
    private ApiResponseRepository repository;

    // Runs every hour (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredCache() {
        repository.deleteExpired(Instant.now());
        System.out.println("Cleaned expired cache entries.");
    }
}