package com.example.payment.service.online;

import java.io.IOException;
import java.net.URISyntaxException;

public interface OnlinePaymentService {
    void makeOnlinePayment(String message) throws IOException, URISyntaxException;
}
