package com.example.payment.service.payment.ofline;

import com.example.payment.dto.ErrorJsonDTO;
import com.example.payment.enums.ErrorType;
import com.example.payment.external.ErrorLogService;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.model.Account;
import com.example.payment.model.Payment;
import com.example.payment.repository.AccountRepository;
import com.example.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;

@Service
public class OfflinePaymentServiceImpl implements OfflinePaymentService {
    private static final String OFFLINE_TOPIC = "offline";
    private static final String GROUP_ID = "payment";

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ErrorLogService errorLogService;

    public OfflinePaymentServiceImpl(AccountRepository accountRepository, PaymentRepository paymentRepository, PaymentMapper paymentMapper, ErrorLogService errorLogService) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.errorLogService = errorLogService;
    }

    @Override
    @KafkaListener(topics = OFFLINE_TOPIC, groupId = GROUP_ID)
    public void makeOfflinePayment(String message) throws JsonProcessingException, URISyntaxException {
        Payment payment = paymentMapper.mapPaymentMessageToPayment(message);
        paymentRepository.save(payment);
        updateAccount(payment);
    }

    private void updateAccount(Payment payment) throws URISyntaxException {
        Account account = payment.getAccount();

        if (account != null) {
            Date updatedLastPaymentDate = null;
            Optional<Payment> paymentOptional = paymentRepository.findById(payment.getPaymentId());
            if (paymentOptional.isPresent())
                updatedLastPaymentDate = paymentOptional.get().getCreatedOn();
            account.setLastPaymentDate(updatedLastPaymentDate);
            accountRepository.save(account);
            accountRepository.findById(account.getAccountId());
        } else {
            ErrorJsonDTO dto = new ErrorJsonDTO();
            dto.setPaymentId(payment.getPaymentId());
            dto.setError(ErrorType.NETWORK.getErrorType());
            dto.setErrorDescription("There is no account related with payment");
            errorLogService.errorLog(dto);
        }
    }

}
