package com.example.payment.dto;

import com.example.payment.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentJsonDTO {
    @JsonProperty(value = "payment_id")
    private String paymentId;
    @JsonProperty(value = "account_id")
    private String accountId;
    @JsonProperty(value = "payment_type")
    private PaymentType paymentType;
    @JsonProperty(value = "credit_card")
    private String creditCard;
    private String amount;
}
