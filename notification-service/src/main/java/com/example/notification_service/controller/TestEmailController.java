package com.example.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.notification_service.dto.OrderCreatedEvent;
import com.example.notification_service.service.EmailService;

@RestController
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail() {

        OrderCreatedEvent event = new OrderCreatedEvent(
                1L,
                1L,
                "parth110301@gmail.com",
                "Parth Lakhani",        // ← add userName
                500.0
        );

        emailService.sendOrderConfirmation(event);

        return "Email triggered";
    }
}
