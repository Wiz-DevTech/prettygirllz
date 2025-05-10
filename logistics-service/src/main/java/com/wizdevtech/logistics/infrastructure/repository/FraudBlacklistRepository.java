package com.wizdevtech.logistics.infrastructure.repository;

import com.wizdevtech.logistics.infrastructure.entity.FraudBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistRepository extends JpaRepository<FraudBlacklistEntity, Long> {
    Optional<FraudBlacklistEntity> findByPhone(String phone);
    boolean existsByPhone(String phone);
}