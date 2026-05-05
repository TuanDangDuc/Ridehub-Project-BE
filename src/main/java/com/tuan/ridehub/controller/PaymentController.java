package com.tuan.ridehub.controller;

import com.tuan.ridehub.dto.request.PaymentDtoRequest;
import com.tuan.ridehub.dto.response.PaymentDtoResponse;
import com.tuan.ridehub.dto.request.SePayWebhookDto;
import com.tuan.ridehub.service.PaymentService;
import com.tuan.ridehub.service.SePayService;
import com.tuan.ridehub.service.SePayGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final SePayService sePayService;
    private final SePayGatewayService sePayGatewayService;

    @org.springframework.beans.factory.annotation.Value("${sepay.merchant-id}")
    private String merchantId;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<?> sePayWebhook(@RequestBody SePayWebhookDto payload) {
        log.info("=== [WEBHOOK] Received SePay IPN ===");
        log.info("Payload: {}", payload);
        try {
            sePayService.processWebhook(payload);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            log.error("Error processing SePay webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok().body("{\"success\": false}");
        }
    }

    @GetMapping("/checkout-redirect")
    public ResponseEntity<String> checkoutRedirect(
            @RequestParam Double amount,
            @RequestParam UUID userId) {
        
        log.info("=== [GATEWAY] Initiate Redirect ===");
        log.info("Amount: {}, UserId: {}", amount, userId);
        
        String invoiceNumber = "TOPUP-" + System.currentTimeMillis();
        String description = "NAP CREDIT " + userId;
        
        java.util.Map<String, String> fields = new java.util.HashMap<>();
        fields.put("merchant", merchantId);
        fields.put("operation", "PURCHASE");
        fields.put("order_invoice_number", invoiceNumber);
        fields.put("order_amount", String.valueOf(amount.intValue()));
        fields.put("currency", "VND");
        fields.put("order_description", description);
        fields.put("success_url", "https://anhchuno.id.vn/payment/success");
        fields.put("error_url", "https://anhchuno.id.vn/payment/error");
        fields.put("cancel_url", "https://anhchuno.id.vn/payment/cancel");

        String signature = sePayGatewayService.generateSignature(fields);
        log.info("Generated Signature: {}", signature);
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Redirecting to SePay...</title></head>");
        html.append("<body onload='document.forms[0].submit()'>");
        html.append("<h3>Đang chuyển hướng tới cổng thanh toán SePay...</h3>");
        html.append("<form action='https://pay.sepay.vn/checkout' method='POST'>");
        fields.forEach((k, v) -> {
            html.append("<input type='hidden' name='").append(k).append("' value='").append(v).append("'>");
        });
        html.append("<input type='hidden' name='signature' value='").append(signature).append("'>");
        html.append("</form></body></html>");

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html.toString());
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
