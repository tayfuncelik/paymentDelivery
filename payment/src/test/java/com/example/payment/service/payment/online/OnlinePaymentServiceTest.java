package com.example.payment.service.payment.online;

import com.example.payment.dto.PaymentJsonDTO;
import com.example.payment.enums.PaymentType;
import com.example.payment.external.ErrorLogService;
import com.example.payment.external.PaymentGateAwayService;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.model.Account;
import com.example.payment.model.Payment;
import com.example.payment.repository.AccountRepository;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.online.OnlinePaymentService;
import com.example.payment.service.online.OnlinePaymentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OnlinePaymentServiceTest {

    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private PaymentMapper paymentMapper = Mockito.mock(PaymentMapper.class);
    private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private PaymentGateAwayService paymentGateAwayService = Mockito.mock(PaymentGateAwayService.class);
    private ErrorLogService errorLogService = Mockito.mock(ErrorLogService.class);

    private final OnlinePaymentService onlinePaymentService = new OnlinePaymentServiceImpl(accountRepository, paymentRepository, paymentMapper, paymentGateAwayService, errorLogService);

    @Test
    public void makeOnlinePaymentTest() {
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

        Mockito.when(paymentMapper.mapPaymentJsonToPayment(any())).thenReturn(p);

        try {
            when(paymentGateAwayService.validatePayment(any())).thenReturn(HttpStatus.OK);
            ObjectMapper mapper = new ObjectMapper();
            payment = mapper.writeValueAsString(p);
            Mockito.when(paymentRepository.findById(p.getPaymentId())).thenReturn(Optional.of(p));
            onlinePaymentService.makeOnlinePayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void makeOnlinePaymentAccountErrorTest() {
        String payment = "";
        Payment p = new Payment();
        p.setPaymentId("1");
        p.setAccount(null);
        p.setAmount(BigDecimal.ONE);
        p.setCreditCard("1234");
        p.setPaymentType(PaymentType.OFFLINE);
        try {
            Mockito.when(paymentMapper.mapPaymentJsonToPayment(any())).thenReturn(p);
            when(paymentGateAwayService.validatePayment(any())).thenReturn(HttpStatus.OK);
            onlinePaymentService.makeOnlinePayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void makeOnlinePaymentErrorTest() {

        try {
            PaymentJsonDTO paymentJsonDTO = mock(PaymentJsonDTO.class);
            Mockito.when(paymentMapper.mapPaymentMessageToPaymentJson(any())).thenReturn(paymentJsonDTO);
            when(paymentGateAwayService.validatePayment(any())).thenReturn(HttpStatus.BAD_REQUEST);
            onlinePaymentService.makeOnlinePayment(any());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
