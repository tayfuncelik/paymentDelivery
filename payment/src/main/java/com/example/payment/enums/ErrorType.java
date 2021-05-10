package com.example.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    DATABASE("database"),
    NETWORK("network"),
    OTHER("other");
    private final String errorType;
}
