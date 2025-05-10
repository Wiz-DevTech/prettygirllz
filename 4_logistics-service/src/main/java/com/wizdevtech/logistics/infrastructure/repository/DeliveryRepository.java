package com.wizdevtech.logistics.infrastructure.repository;

import com.wizdevtech.logistics.infrastructure.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
    Optional<DeliveryEntity> findByOrderId(String orderId);

    List<DeliveryEntity> findByStatus(String status);

    @Query(value = "SELECT version()", nativeQuery = true)
    String getDatabaseProductName();
}