package com.example.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorJsonDTO {
    private String paymentId;
    private String error;
    private String errorDescription;
}
