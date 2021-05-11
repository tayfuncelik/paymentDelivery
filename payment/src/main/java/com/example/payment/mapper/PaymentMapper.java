package com.example.payment.mapper;

import com.example.payment.dto.PaymentJsonDTO;
import com.example.payment.enums.PaymentType;
import com.example.payment.model.Account;
import com.example.payment.model.Payment;
import com.example.payment.repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PaymentMapper {
    private final AccountRepository accountRepository;

    @Autowired
    public PaymentMapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Payment mapPaymentMessageToPayment(String message) throws JsonProcessingException {
        return mapPaymentJsonToPayment(mapPaymentMessageToPaymentJson(message));
    }

    public PaymentJsonDTO mapPaymentMessageToPaymentJson(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(message, PaymentJsonDTO.class);
    }

    public Payment mapPaymentJsonToPayment(PaymentJsonDTO paymentJsonDTO) {
        Payment payment = new Payment();
        payment.setAccount(getAccount(paymentJsonDTO));
        payment.setPaymentType(paymentJsonDTO.getPaymentType());
        payment.setCreditCard(paymentJsonDTO.getCreditCard());
        payment.setAmount(BigDecimal.valueOf(Integer.parseInt(paymentJsonDTO.getAmount())));
        return payment;
    }

    private Account getAccount(PaymentJsonDTO paymentJson) {
        if (PaymentType.OFFLINE.getName().equals(paymentJson.getPaymentType())) {
            return null;
        } else {
            Optional<Account> account =accountRepository.findById(Integer.parseInt(paymentJson.getAccountId()));
            return account.orElse(null);
        }
    }
}