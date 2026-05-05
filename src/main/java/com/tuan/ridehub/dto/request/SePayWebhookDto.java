package com.tuan.ridehub.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SePayWebhookDto {

    // === Payment Gateway IPN fields (nested) ===
    private Long timestamp;

    @JsonProperty("notification_type")
    private String notificationType;

    private SePayOrder order;
    private SePayTransaction transaction;

    // === Bank Monitoring Webhook fields (flat) ===
    private Long id;
    private String gateway;
    private String accountNumber;
    private String code;
    private String content;
    private String transferType;
    private Long transferAmount;
    private Long accumulated;
    private String subAccount;
    private String referenceCode;
    private String description;

    /// True nếu đây là webhook từ SePay Bank Monitoring (flat format)
    public boolean isBankWebhook() {
        return transferType != null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SePayOrder {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("order_status")
        private String orderStatus;

        @JsonProperty("order_amount")
        private Double orderAmount;

        @JsonProperty("order_invoice_number")
        private String orderInvoiceNumber;

        @JsonProperty("order_description")
        private String orderDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SePayTransaction {
        private String id;

        @JsonProperty("payment_method")
        private String paymentMethod;

        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("transaction_date")
        private String transactionDate;

        @JsonProperty("transaction_status")
        private String transactionStatus;

        @JsonProperty("transaction_amount")
        private Double transactionAmount;
    }
}
