package com.wizdevtech.controller;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public String health() {
        try (Connection conn = dataSource.getConnection()) {
            return "DB connection successful!";
        } catch (Exception e) {
            return "DB connection failed: " + e.getMessage();
        }
    }
}