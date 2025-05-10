package com.wizdevtech.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_responses")
public class ApiResponse {
    @Id
    @Column(length = 255)
    private String id;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    // Using a plain String with no converter
    @Column(name = "response_json", columnDefinition = "text")
    private String responseJson;

    @Column(name = "expiration")
    private Instant expiry;

    // Constructors
    public ApiResponse() {}

    public ApiResponse(String id, String apiName, String responseJson, Instant expiry) {
        this.id = id;
        this.apiName = apiName;
        this.responseJson = responseJson;
        this.expiry = expiry;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }
}