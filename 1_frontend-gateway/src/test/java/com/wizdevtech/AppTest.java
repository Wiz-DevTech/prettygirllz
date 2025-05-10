package com.wizdevtech;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = GatewayApplication.class)
@ActiveProfiles("test")
class AppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Verify the application context loads successfully
        assertNotNull(context);
        assertTrue(context.containsBean("gatewayApplication"));
    }
}
