package com.example.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.notification_service.dto.OrderCreatedEvent;
import com.example.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void consume(String message) {

        System.out.println("RAW EVENT: " + message);

        ObjectMapper mapper = new ObjectMapper();

        try {
            OrderCreatedEvent event =
                    mapper.readValue(message, OrderCreatedEvent.class);

            emailService.sendOrderConfirmation(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
