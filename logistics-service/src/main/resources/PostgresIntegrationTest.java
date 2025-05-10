package com.wizdevtech.logistics.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.wizdevtech.logistics.core.model.DeliveryStatus;
import com.wizdevtech.logistics.infrastructure.entity.DeliveryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.wizdevtech.logistics.application.LogisticsApp;

@SpringBootTest(classes = LogisticsApp.class)
@ActiveProfiles("integration-test")
@Transactional
public class PostgresIntegrationTest {

    @Autowired
    private DeliveryRepository DeliveryRepository;

    @Test
    public void testDatabaseIsPostgreSQL() {
        // Verify we're using PostgreSQL
        String productName = deliveryRepository.getDatabaseProductName();
        assertEquals("PostgreSQL", productName);
    }

    @Test
    public void testDeliveryPersistence() {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setTrackingNumber("PGTEST123");
        delivery.setStatus(DeliveryStatus.PENDING);

        DeliveryEntity saved = deliveryRepository.save(delivery);
        assertNotNull(saved.getId());

        DeliveryEntity found = deliveryRepository.findById(saved.getId()).orElse(null);
        assertEquals("PGTEST123", found.getTrackingNumber());
    }
}