package com.wizdevtech.repository;

import com.wizdevtech.model.SsrCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;

public interface SsrCacheRepository extends JpaRepository<SsrCache, String> {
    Optional<SsrCache> findByRouteAndExpiryAfter(String route, Instant expiry);
}