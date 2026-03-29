package com.example.notification_service.consumer;


import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;

import org.springframework.stereotype.Component;


import com.example.notification_service.dto.OrderCreatedEvent;
import com.example.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailService emailService;

    @RetryableTopic(attempts = "3")
    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void consume(String message) {
        log.info("RAW EVENT received: {}", message);

        ObjectMapper mapper = new ObjectMapper();

        try {
            OrderCreatedEvent event = mapper.readValue(message, OrderCreatedEvent.class);
            emailService.sendOrderConfirmation(event);
            log.info("Email sent successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process event: {}", e.getMessage());
            // throwing exception triggers retry
            throw new RuntimeException("Failed to process order event", e);
        }
    }

    // called when all 3 retries are exhausted
    @DltHandler
    public void handleDlt(String message) {
        log.error("Event failed all retries — moved to DLT: {}", message);
        // you can save this to DB or alert ops team here
    }
}