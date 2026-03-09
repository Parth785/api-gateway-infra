package com.example.notification_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.notification_service.dto.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(OrderCreatedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getUserEmail());
        message.setSubject("Order Confirmation");
        message.setText(
                "Hello,\n\n" +
                "Your order with ID " + event.getOrderId() +
                " has been placed successfully.\n\n" +
                "Total Amount: " + event.getTotalPrice()
        );

        mailSender.send(message);

        System.out.println("✅ Email sent to " + event.getUserEmail());
    }
}
