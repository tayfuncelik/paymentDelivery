package com.example.payment.service.payment.ofline;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URISyntaxException;

public interface OfflinePaymentService {
    void makeOfflinePayment(String message) throws JsonProcessingException, URISyntaxException;
}
