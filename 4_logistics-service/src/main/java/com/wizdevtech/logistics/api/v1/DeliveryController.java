package com.wizdevtech.logistics.api.v1;

import com.wizdevtech.logistics.api.dto.request.CreateDeliveryRequest;
import com.wizdevtech.logistics.api.dto.response.DeliveryResponse;
import com.wizdevtech.logistics.core.model.Delivery;
import com.wizdevtech.logistics.core.model.DeliveryStatus;
import com.wizdevtech.logistics.core.model.Recipient;
import com.wizdevtech.logistics.core.service.DeliveryService;
import com.wizdevtech.logistics.orchestration.fraud.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final FraudDetectionService fraudDetectionService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody CreateDeliveryRequest request) {
        Recipient recipient = Recipient.builder()
                .name(request.getRecipient().getName())
                .phone(request.getRecipient().getPhone())
                .email(request.getRecipient().getEmail())
                .build();

        // Check for fraud
        if (fraudDetectionService.isSuspiciousRecipient(recipient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery request flagged for fraud");
        }

        Delivery delivery = deliveryService.createDelivery(
                request.getOrderId(),
                request.getDestinationType(),
                recipient
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(delivery));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable String orderId) {
        return deliveryService.findByOrderId(orderId)
                .map(delivery -> ResponseEntity.ok(mapToResponse(delivery)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @RequestParam(required = false) String status) {
        List<Delivery> deliveries;

        if (status != null) {
            try {
                DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
                deliveries = deliveryService.findByStatus(deliveryStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
            }
        } else {
            deliveries = deliveryService.findByStatus(DeliveryStatus.CREATED);
        }

        List<DeliveryResponse> response = deliveries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        try {
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());

            return deliveryService.updateStatus(orderId, deliveryStatus)
                    .map(delivery -> ResponseEntity.ok(mapToResponse(delivery)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
        }
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse.RecipientDto recipientDto = DeliveryResponse.RecipientDto.builder()
                .name(delivery.getRecipient().getName())
                .phone(delivery.getRecipient().getPhone())
                .email(delivery.getRecipient().getEmail())
                .build();

        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .destinationType(delivery.getDestinationType())
                .status(delivery.getStatus())
                .recipient(recipientDto)
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
