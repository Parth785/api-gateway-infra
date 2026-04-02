package com.example.order.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String cardNumber;
    private String expiry;
    private String cvv;
    private String cardHolder;
}