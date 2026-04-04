package com.example.notification_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.notification_service.dto.OrderCreatedEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

	 @Value("${spring.kafka.bootstrap-servers}")
	 private String bootstrapServers;
	 
	 @Bean
	    public ConsumerFactory<String, OrderCreatedEvent> consumerFactory() {
	        JsonDeserializer<OrderCreatedEvent> deserializer =
	                new JsonDeserializer<>(OrderCreatedEvent.class);
	        deserializer.addTrustedPackages("*");

	        Map<String, Object> config = new HashMap<>();
	        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // ← uses yml value
	        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
	        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

	        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
	    }

	    @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> kafkaListenerContainerFactory() {
	        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
	                new ConcurrentKafkaListenerContainerFactory<>();
	        factory.setConsumerFactory(consumerFactory());
	        return factory;
	    }
	}

