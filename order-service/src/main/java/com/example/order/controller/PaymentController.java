package com.example.order.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.dto.OrderStatus;
import com.example.order.dto.PaymentRequest;
import com.example.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request) {

        try {
            // validate card format
            if (!isValidCard(request.getCardNumber())) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                            "success", false,
                            "message", "Invalid card number"
                        ));
            }

            if (!isValidExpiry(request.getExpiry())) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                            "success", false,
                            "message", "Card has expired"
                        ));
            }

            // simulate payment processing delay
            Thread.sleep(1500);

            // update order status to PROCESSING
            orderService.updateStatus(orderId, OrderStatus.PROCESSING);

            log.info("Payment processed for order #{}", orderId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful",
                "orderId", orderId,
                "transactionId", generateTransactionId()
            ));

        } catch (Exception e) {
            log.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                        "success", false,
                        "message", "Payment processing failed"
                    ));
        }
    }

    private boolean isValidCard(String cardNumber) {
        if (cardNumber == null) return false;
        String cleaned = cardNumber.replaceAll("\\s", "");
        return cleaned.matches("\\d{16}");
    }

    private boolean isValidExpiry(String expiry) {
        if (expiry == null) return false;
        if (!expiry.matches("\\d{2}/\\d{2}")) return false;
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000;
        java.time.YearMonth cardExpiry = java.time.YearMonth.of(year, month);
        return cardExpiry.isAfter(java.time.YearMonth.now());
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
}