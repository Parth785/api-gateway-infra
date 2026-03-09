package com.example.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.ProductResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.event.OrderCreatedEvent;
import com.example.order.event.OrderEventProducer;
import com.example.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	 private final OrderRepository orderRepository;
	 private final RestTemplate restTemplate;
	 private final OrderEventProducer orderEventProducer;

	    private static final String USER_SERVICE_URL =
	            "http://localhost:8085/users/";

	    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

	        Order order = new Order();
	        order.setUserId(userId);

	        BigDecimal total = BigDecimal.ZERO;

	        List<OrderItem> items = new ArrayList<>();

	        for (OrderItemRequest itemReq : request.getItems()) {

	            ProductResponse product =
	                    restTemplate.getForObject(
	                            "http://localhost:8087/products/" + itemReq.getProductId(),
	                            ProductResponse.class);

	            BigDecimal itemPrice = product.getPrice();

	            OrderItem item = new OrderItem();
	            item.setProductId(product.getId());
	            item.setQuantity(itemReq.getQuantity());
	            item.setPrice(itemPrice);
	            item.setOrder(order);

	            items.add(item);

	            total = total.add(itemPrice.multiply(
	                    BigDecimal.valueOf(itemReq.getQuantity())));
	        }

	        order.setItems(items);
	        order.setTotalPrice(total);

	        Order saved = orderRepository.save(order);
	        
	        OrderCreatedEvent event = new OrderCreatedEvent(
	                saved.getId(),
	                saved.getUserId(),
	                saved.getTotalPrice().doubleValue()
	        );
	        
	        orderEventProducer.publishOrderCreated(event);

	        return new OrderResponse(saved.getId(), saved.getUserId(), saved.getStatus());
	    }
    public List<OrderResponse> getOrdersByUser(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getUserId(),
                        o.getStatus()))
                .toList();
    }

    public OrderResponse updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getStatus()
        );
    }
}