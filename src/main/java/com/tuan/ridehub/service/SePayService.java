package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.PaymentRepository;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SePayService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    @Value("${sepay.webhook-secret:sepay_secret_placeholder}")
    private String sepayWebhookSecret;

    public boolean verifyToken(String authToken) {
        return sepayWebhookSecret.equals(authToken);
    }

    @Transactional
    public void processWebhook(SePayWebhookDto status) {
        log.info("Processing SePay Webhook for transaction: {}", status.getReferenceNumber());

        // Extract User ID from content (Expected format: HUB <UUID>)
        UUID userId = extractUserId(status.getContent());
        if (userId == null) {
            log.error("Could not extract User ID from content: {}", status.getContent());
            return;
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Create Payment record
        Payment payment = Payment.builder()
                .amount(status.getAmount())
                .paymentMethod("SEPAY_BANK_TRANSFER")
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData(status.toString()) // Store full payload for audit
                .build();

        paymentRepository.save(payment);

        // Update user credit
        userService.addCredit(userId, status.getAmount());
        log.info("Successfully added {} credit to user {}", status.getAmount(), userId);
    }

    private UUID extractUserId(String content) {
        if (content == null) return null;
        
        // Simple regex to find UUID in content
        // Adjust prefix if needed (e.g., HUB)
        Pattern pattern = Pattern.compile("([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            try {
                return UUID.fromString(matcher.group(1));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
