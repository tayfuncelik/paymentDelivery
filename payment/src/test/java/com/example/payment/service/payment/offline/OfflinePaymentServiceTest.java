package com.example.payment.service.payment.offline;

import com.example.payment.enums.PaymentType;
import com.example.payment.external.ErrorLogService;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.model.Account;
import com.example.payment.model.Payment;
import com.example.payment.repository.AccountRepository;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.ofline.OfflinePaymentService;
import com.example.payment.service.ofline.OfflinePaymentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class OfflinePaymentServiceTest {

    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private PaymentMapper paymentMapper = Mockito.mock(PaymentMapper.class);
    private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private ErrorLogService errorLogService = Mockito.mock(ErrorLogService.class);

    private final OfflinePaymentService offlinePaymentService = new OfflinePaymentServiceImpl(accountRepository, paymentRepository, paymentMapper, errorLogService);

    @Test
    public void makeOfflinePaymentTest() {
        String payment = "";
        Payment p = new Payment();
        p.setPaymentId("1");
        Account account = new Account();
        account.setAccountId(5);
        Optional<Account> optional = Optional.of(account);
        Mockito.when(accountRepository.findById(5)).thenReturn(optional);

        p.setAccount(optional.get());
        p.setAmount(BigDecimal.ONE);
        p.setCreditCard("1234");
        p.setPaymentType(PaymentType.ONLINE);
        Mockito.when(paymentRepository.findById(p.getPaymentId())).thenReturn(Optional.of(p));

        ObjectMapper mapper = new ObjectMapper();
        try {
            Mockito.when(paymentMapper.mapPaymentMessageToPayment(any())).thenReturn(p);
            payment = mapper.writeValueAsString(p);
            offlinePaymentService.makeOfflinePayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void makeOfflinePaymentErrorPaymentTest() {
        String payment = "";
        Payment p = new Payment();
        p.setPaymentId("1");
        p.setAmount(BigDecimal.ONE);
        p.setCreditCard("1234");
        p.setPaymentType(null);

        try {
            Mockito.when(paymentMapper.mapPaymentMessageToPayment(any())).thenReturn(p);
            offlinePaymentService.makeOfflinePayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
