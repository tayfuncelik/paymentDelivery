package com.example.payment.external;

import com.example.payment.dto.PaymentJsonDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@AllArgsConstructor
public class PaymentGateAwayService {

    private static String url;
    private final WebClient webClient;

    @Autowired
    public PaymentGateAwayService(@Value("${external.payment.gateaway}") String url, WebClient webClient) {
        this.url = url;
        this.webClient = webClient;
    }

    public HttpStatus validatePayment(PaymentJsonDTO dto) throws URISyntaxException {
        return webClient
                .post()
                .uri(new URI(url))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .blockOptional()
                .get();
    }


}
