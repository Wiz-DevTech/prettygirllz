package com.wizdevtech.logistics.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    private Long id;
    private String orderId;
    private DestinationType destinationType;
    private DeliveryStatus status;
    private Recipient recipient;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}