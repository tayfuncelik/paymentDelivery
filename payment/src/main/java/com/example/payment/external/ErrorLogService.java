package com.example.payment.external;

import com.example.payment.dto.ErrorJsonDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@AllArgsConstructor
public class ErrorLogService {

    @Value("${external.error.log.url}")
    private static String url;
    private final WebClient webClient;

    public void errorLog(ErrorJsonDTO dto) throws URISyntaxException {
        webClient.post()
                .uri(new URI(url))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto);
    }
}
