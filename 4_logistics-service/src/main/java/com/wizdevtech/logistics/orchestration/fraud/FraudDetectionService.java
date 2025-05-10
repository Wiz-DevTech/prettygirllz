package com.wizdevtech.logistics.orchestration.fraud;

import com.wizdevtech.logistics.core.model.Recipient;
import com.wizdevtech.logistics.infrastructure.repository.FraudBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudBlacklistRepository fraudBlacklistRepository;

    @Value("${fraud.threshold:0.85}")
    private double fraudThreshold;

    public boolean isSuspiciousRecipient(Recipient recipient) {
        // Check if phone is in blacklist
        if (recipient.getPhone() != null) {
            boolean isBlacklisted = fraudBlacklistRepository.existsByPhone(recipient.getPhone());
            if (isBlacklisted) {
                return true;
            }
        }

        // Here you could add more sophisticated fraud detection logic
        // This is just a placeholder implementation
        return false;
    }
}