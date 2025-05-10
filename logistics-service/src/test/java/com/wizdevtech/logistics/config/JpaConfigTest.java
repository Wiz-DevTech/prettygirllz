package com.wizdevtech.logistics.config; // Adjust package if needed

import org.junit.jupiter.api.Test;
import com.wizdevtech.logistics.application.LogisticsApp;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.hibernate.SessionFactory;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

import com.wizdevtech.logistics.application.LogisticsApp; // Verify correct package

@SpringBootTest(classes = LogisticsApp.class)
@ActiveProfiles("test")
public class JpaConfigTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;

    @Test
    public void testEntityManagerFactoryCreation() {
        assertNotNull(entityManagerFactory);
    }

    @Test
    public void testDataSourceConfiguration() {
        assertNotNull(dataSource);
    }

    @Test
    public void testJpaVendorAdapterConfiguration() {
        assertNotNull(jpaVendorAdapter);
        assertTrue(jpaVendorAdapter instanceof HibernateJpaVendorAdapter);
    }

    @Test
    public void testHibernateProperties() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Map<String, Object> properties = sessionFactory.getProperties();

        assertNotNull(properties.get("hibernate.dialect"));
        assertEquals("create-drop", properties.get("hibernate.hbm2ddl.auto"));
    }
}