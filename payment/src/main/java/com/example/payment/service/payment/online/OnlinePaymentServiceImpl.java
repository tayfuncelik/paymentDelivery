package com.example.payment.service.payment.online;

import com.example.payment.dto.ErrorJsonDTO;
import com.example.payment.dto.PaymentJsonDTO;
import com.example.payment.enums.ErrorType;
import com.example.payment.external.ErrorLogService;
import com.example.payment.external.PaymentGateAwayService;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.model.Account;
import com.example.payment.model.Payment;
import com.example.payment.repository.AccountRepository;
import com.example.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;

@Service
public class OnlinePaymentServiceImpl implements OnlinePaymentService {
    private static final String ONLINE_TOPIC = "online";
    private static final String GROUP_ID = "payment";

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGateAwayService paymentGateAwayService;
    private final ErrorLogService errorLogService;

    public OnlinePaymentServiceImpl(AccountRepository accountRepository, PaymentRepository paymentRepository, PaymentMapper paymentMapper, PaymentGateAwayService paymentGateAwayService, ErrorLogService errorLogService) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentGateAwayService = paymentGateAwayService;
        this.errorLogService = errorLogService;
    }

    @Override
    @KafkaListener(topics = ONLINE_TOPIC, groupId = GROUP_ID)
    public void makeOnlinePayment(String message) throws IOException, URISyntaxException {
        PaymentJsonDTO paymentJsonDTO = paymentMapper.mapPaymentMessageToPaymentJson(message);

        if (isPaymentValid(paymentJsonDTO)) {
            Payment payment = paymentMapper.mapPaymentJsonToPayment(paymentJsonDTO);
            paymentRepository.save(payment);
            updateAccount(payment);
        } else {
            ErrorJsonDTO dto = new ErrorJsonDTO();
            dto.setPaymentId(paymentJsonDTO.getPaymentId());
            dto.setError(ErrorType.NETWORK.getErrorType());
            dto.setErrorDescription("Invalid Payment");
            errorLogService.errorLog(dto);
        }
    }

    private boolean isPaymentValid(PaymentJsonDTO paymentJsonDTO) throws URISyntaxException {
        HttpStatus isValidPaymentHttpStatus = paymentGateAwayService.validatePayment(paymentJsonDTO);
        return isValidPaymentHttpStatus.is2xxSuccessful();
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
