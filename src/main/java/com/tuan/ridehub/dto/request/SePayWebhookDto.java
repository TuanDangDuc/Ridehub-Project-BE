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

    private Long timestamp;

    @JsonProperty("notification_type")
    private String notificationType;

    private SePayOrder order;
    private SePayTransaction transaction;
    private Object customer;
    private Object agreement;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SePayOrder {
        private String id;

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("order_status")
        private String orderStatus;

        @JsonProperty("order_currency")
        private String orderCurrency;

        @JsonProperty("order_amount")
        private Long orderAmount;

        @JsonProperty("order_invoice_number")
        private String orderInvoiceNumber;

        @JsonProperty("custom_data")
        private Object customData;

        @JsonProperty("user_agent")
        private Object userAgent;

        @JsonProperty("ip_address")
        private String ipAddress;

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

        @JsonProperty("transaction_type")
        private String transactionType;

        @JsonProperty("transaction_date")
        private String transactionDate;

        @JsonProperty("transaction_status")
        private String transactionStatus;

        @JsonProperty("transaction_amount")
        private Long transactionAmount;

        @JsonProperty("transaction_currency")
        private String transactionCurrency;

        @JsonProperty("authentication_status")
        private String authenticationStatus;

        @JsonProperty("card_number")
        private String cardNumber;

        @JsonProperty("card_holder_name")
        private String cardHolderName;

        @JsonProperty("card_expiry")
        private String cardExpiry;

        @JsonProperty("card_funding_method")
        private String cardFundingMethod;

        @JsonProperty("card_brand")
        private String cardBrand;
    }
}
