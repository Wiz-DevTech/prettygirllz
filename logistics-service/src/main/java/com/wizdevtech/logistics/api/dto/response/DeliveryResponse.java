package com.wizdevtech.logistics.api.dto.response;

import com.wizdevtech.logistics.core.model.DeliveryStatus;
import com.wizdevtech.logistics.core.model.DestinationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private Long id;
    private String orderId;
    private DestinationType destinationType;
    private DeliveryStatus status;
    private RecipientDto recipient;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipientDto {
        private String name;
        private String phone;
        private String email;
    }
}