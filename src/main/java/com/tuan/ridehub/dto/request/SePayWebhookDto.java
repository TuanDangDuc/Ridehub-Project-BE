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
        private Object id;

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("order_status")
        private String orderStatus;

        @JsonProperty("order_currency")
        private Object orderCurrency;

        @JsonProperty("order_amount")
        private String orderAmount;

        @JsonProperty("order_invoice_number")
        private String orderInvoiceNumber;

        @JsonProperty("custom_data")
        private Object customData;

        @JsonProperty("user_agent")
        private Object userAgent;

        @JsonProperty("ip_address")
        private Object ipAddress;

        @JsonProperty("order_description")
        private String orderDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SePayTransaction {
        private Object id;

        @JsonProperty("payment_method")
        private String paymentMethod;

        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("transaction_type")
        private Object transactionType;

        @JsonProperty("transaction_date")
        private Object transactionDate;

        @JsonProperty("transaction_status")
        private String transactionStatus;

        @JsonProperty("transaction_amount")
        private String transactionAmount;

        @JsonProperty("transaction_currency")
        private Object transactionCurrency;

        @JsonProperty("authentication_status")
        private Object authenticationStatus;

        @JsonProperty("card_number")
        private Object cardNumber;

        @JsonProperty("card_holder_name")
        private Object cardHolderName;

        @JsonProperty("card_expiry")
        private Object cardExpiry;

        @JsonProperty("card_funding_method")
        private Object cardFundingMethod;

        @JsonProperty("card_brand")
        private Object cardBrand;
    }
}
