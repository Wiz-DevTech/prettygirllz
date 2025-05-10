package com.wizdevtech.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ssr_cache")  // Different table name
public class SsrCache {

    @Id
    @Column(name = "route", length = 255)  // Using route as primary key
    private String route;

    @Column(name = "html", columnDefinition = "TEXT", nullable = false)
    private String html;

    @Column(name = "expiry", nullable = false)
    private Instant expiry;

    // Constructors
    public SsrCache() {}

    public SsrCache(String route, String html, Instant expiry) {
        this.route = route;
        this.html = html;
        this.expiry = expiry;
    }

    // Getters and Setters
    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }
}