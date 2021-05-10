package com.example.payment.enums;

public enum PaymentType {
    OFFLINE("offline"),
    ONLINE("online");

    private String name;

    PaymentType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
