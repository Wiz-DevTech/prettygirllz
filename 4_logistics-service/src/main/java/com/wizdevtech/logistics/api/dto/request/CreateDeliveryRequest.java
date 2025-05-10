package com.wizdevtech.logistics.api.dto.request;

import com.wizdevtech.logistics.core.model.DestinationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequest {
    private String orderId;
    private DestinationType destinationType;
    private RecipientDto recipient;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientDto {
        private String name;
        private String phone;
        private String email;
    }
}
