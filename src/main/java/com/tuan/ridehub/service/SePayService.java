package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.PaymentRepository;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final java.util.Map<String, UUID> orderUserMapping = new java.util.concurrent.ConcurrentHashMap<>();

    public void saveMapping(String sepayOrderId, UUID userId) {
        log.info("Saving mapping: {} -> {}", sepayOrderId, userId);
        orderUserMapping.put(sepayOrderId, userId);
    }

    @Transactional
    public void processWebhook(SePayWebhookDto webhook) {
        if (webhook.isBankWebhook()) {
            processBankWebhook(webhook);
        } else {
            processGatewayIPN(webhook);
        }
    }

    /**
     * Xử lý Webhook từ SePay Bank Monitoring (Dạng phẳng)
     * Thường dùng khi user chuyển khoản thủ công hoặc biến động số dư.
     */
    private void processBankWebhook(SePayWebhookDto webhook) {
        log.info("=== SePay Bank Webhook (Flat) Received ===");
        log.info("Gateway: {}, Amount: {}, Content: {}", webhook.getGateway(), webhook.getTransferAmount(), webhook.getContent());

        if (!"in".equalsIgnoreCase(webhook.getTransferType())) {
            log.warn("Ignoring OUT transfer type");
            return;
        }

        Double amount = (webhook.getTransferAmount() != null) ? webhook.getTransferAmount().doubleValue() : 0.0;
        if (amount <= 0) return;

        // Ưu tiên trích xuất từ Content (nội dung chuyển khoản)
        UUID userId = extractUserId(webhook.getContent());
        if (userId == null) {
            userId = extractUserId(webhook.getDescription());
        }

        // Nếu không trích xuất được UUID, thử tra cứu từ Mapping cục bộ
        if (userId == null && webhook.getCode() != null) {
            userId = orderUserMapping.get(webhook.getCode());
        }

        // CHIÊU CUỐI: Nếu vẫn không thấy, dùng API Token để hỏi trực tiếp SePay
        if (userId == null && webhook.getCode() != null) {
            try {
                log.info("Proactively looking up transaction {} from SePay API...", webhook.getCode());
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.set("Authorization", "Bearer 8EGFQ6TTGHVRYXBZHJKK8Y4STWZFK3XQ4AXP0CIKJOWOD3VNBG7NCVD1JLS1BFIO");
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

                // Tìm trong danh sách giao dịch của SePay
                String url = "https://my.sepay.vn/userapi/transactions/list?code=" + webhook.getCode();
                org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, java.util.Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    java.util.List<java.util.Map<String, Object>> txs = (java.util.List) response.getBody().get("transactions");
                    if (txs != null && !txs.isEmpty()) {
                        // Lấy mô tả (description) hoặc nội dung (transaction_content) để tìm UUID
                        String remoteContent = (String) txs.get(0).get("transaction_content");
                        userId = extractUserId(remoteContent);
                        if (userId != null) {
                            log.info("Found User ID {} from SePay Remote Transaction Content", userId);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to lookup transaction from SePay API: {}", e.getMessage());
            }
        }

        if (userId == null) {
            log.error("Could not extract User ID from Bank Webhook content: '{}'", webhook.getContent());
            return;
        }

        processCredit(userId, amount, "sepay_bank_id=" + webhook.getId(), "SEPAY_BANK");
    }

    /**
     * Xử lý IPN từ SePay Payment Gateway (Dạng lồng nhau)
     * Thường dùng khi user thực hiện qua luồng Checkout/Redirect.
     */
    private void processGatewayIPN(SePayWebhookDto webhook) {
        log.info("=== SePay Payment Gateway IPN (Nested) Received ===");
        
        if (!"ORDER_PAID".equals(webhook.getNotificationType())) {
            log.warn("Ignoring notification type: {}", webhook.getNotificationType());
            return;
        }

        SePayWebhookDto.SePayOrder order = webhook.getOrder();
        SePayWebhookDto.SePayTransaction transaction = webhook.getTransaction();

        if (order == null || transaction == null) return;

        if (!"CAPTURED".equals(order.getOrderStatus())) return;

        Double amount = order.getOrderAmount();
        UUID userId = extractUserId(order.getOrderDescription());
        
        if (userId == null) {
            userId = extractUserId(order.getOrderInvoiceNumber());
        }

        if (userId == null) {
            log.error("Could not extract User ID from Gateway IPN");
            return;
        }

        processCredit(userId, amount, "sepay_tx=" + transaction.getId(), "SEPAY_GATEWAY");
    }

    private void processCredit(UUID userId, Double amount, String uniqueId, String method) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found: {}", userId);
            return;
        }

        // Chống trùng
        if (paymentRepository.existsByResponseDataContaining(uniqueId)) {
            log.warn("Transaction already processed: {}", uniqueId);
            return;
        }

        Payment payment = Payment.builder()
                .amount(amount)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.PAID)
                .user(user)
                .responseData(uniqueId)
                .build();

        paymentRepository.save(payment);
        userService.addCredit(userId, amount);
        
        log.info("Successfully added {} to user {} via {}", amount, user.getUsername(), method);
    }

    private UUID extractUserId(String text) {
        if (text == null) return null;
        Pattern pattern = Pattern.compile("([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
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
