package com.wizdevtech.logistics.core.service;

import com.wizdevtech.logistics.core.model.Delivery;
import com.wizdevtech.logistics.core.model.DeliveryStatus;
import com.wizdevtech.logistics.core.model.DestinationType;
import com.wizdevtech.logistics.core.model.Recipient;
import com.wizdevtech.logistics.infrastructure.entity.DeliveryEntity;
import com.wizdevtech.logistics.infrastructure.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository DeliveryRepository;

    @Transactional
    public Delivery createDelivery(String orderId, DestinationType destinationType, Recipient recipient) {
        DeliveryEntity entity = DeliveryEntity.builder()
                .orderId(orderId)
                .destinationType(destinationType)
                .status(DeliveryStatus.CREATED)
                .recipientName(recipient.getName())
                .recipientPhone(recipient.getPhone())
                .recipientEmail(recipient.getEmail())
                .build();

        DeliveryEntity saved = DeliveryRepository.save(entity);
        return mapToModel(saved);
    }

    @Transactional(readOnly = true)
    public Optional<Delivery> findByOrderId(String orderId) {
        return DeliveryRepository.findByOrderId(orderId)
                .map(this::mapToModel);
    }

    @Transactional(readOnly = true)
    public List<Delivery> findByStatus(DeliveryStatus status) {
        return DeliveryRepository.findByStatus(status.name())
                .stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Delivery> updateStatus(String orderId, DeliveryStatus status) {
        Optional<DeliveryEntity> entityOpt = DeliveryRepository.findByOrderId(orderId);
        if (entityOpt.isPresent()) {
            DeliveryEntity entity = entityOpt.get();
            entity.setStatus(status);
            return Optional.of(mapToModel(DeliveryRepository.save(entity)));
        }
        return Optional.empty();
    }

    private Delivery mapToModel(DeliveryEntity entity) {
        Recipient recipient = Recipient.builder()
                .name(entity.getRecipientName())
                .phone(entity.getRecipientPhone())
                .email(entity.getRecipientEmail())
                .build();

        return Delivery.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .destinationType(entity.getDestinationType())
                .status(entity.getStatus())
                .recipient(recipient)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}