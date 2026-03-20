package com.example.order.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Async  // runs in background — doesn't block order response
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send("order-created", event);
            log.info("Order event published: {}", event.getOrderId());
        } catch (Exception e) {
            // Kafka failed — log it but don't crash the order
            log.error("Failed to publish order event: {}", e.getMessage());
        }
    }
}