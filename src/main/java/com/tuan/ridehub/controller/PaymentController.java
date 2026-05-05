package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.PaymentDtoRequest;
import com.tuan.ridehub.dto.response.PaymentDtoResponse;
import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.service.PaymentService;
import com.tuan.ridehub.service.SePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@lombok.extern.slf4j.Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final SePayService sePayService;

    @Value("${sepay.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<?> sePayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SePayWebhookDto payload) {
        log.info("=== SePay IPN Request Received ===");
        log.info("Payload: {}", payload);

        // Verify Secret Key
        log.info("Expected Secret Key length: {}", webhookSecret != null ? webhookSecret.length() : "null");
        log.info("Received Auth Header: {}", authHeader != null ? "Present (length " + authHeader.length() + ")" : "Missing");

        if (authHeader == null || !authHeader.equals("Bearer " + webhookSecret)) {
            log.warn("Unauthorized SePay Webhook attempt with header: {}", authHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"success\": false, \"message\": \"Unauthorized\"}");
        }

        try {
            sePayService.processWebhook(payload);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            log.error("Error processing SePay webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok().body("{\"success\": false}");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/pay-with-balance/{tripId}")
    public ResponseEntity<PaymentDtoResponse> payWithBalance(@PathVariable UUID tripId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(paymentService.payTripWithBalance(tripId, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<PaymentDtoResponse> createPayment(@RequestBody PaymentDtoRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/process")
    public ResponseEntity<PaymentDtoResponse> processPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.processPayment(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/fail")
    public ResponseEntity<PaymentDtoResponse> failPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.failPayment(id));
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDtoResponse> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDtoResponse>> getPaymentsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentDtoResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }
}
