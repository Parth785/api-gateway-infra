package com.example.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderItemResponse;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.ProductResponse;
import com.example.order.dto.UserResponse;
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
	    
	 // helper method
	    private OrderResponse mapToResponse(Order order) {
	        List<OrderItemResponse> itemResponses = order.getItems()
	            .stream()
	            .map(i -> new OrderItemResponse(
	                i.getProductId(),
	                i.getQuantity(),
	                i.getPrice()
	            ))
	            .toList();

	        return new OrderResponse(
	            order.getId(),
	            order.getUserId(),
	            order.getStatus(),
	            order.getTotalPrice(),
	            order.getCreatedAt(),
	            itemResponses
	        );
	    }

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
	        
	        UserResponse user = restTemplate.getForObject(
	                "http://localhost:8085/users/" + userId,
	                UserResponse.class
	        );
	        
	        OrderCreatedEvent event = new OrderCreatedEvent(
	        	    saved.getId(),
	        	    saved.getUserId(),
	        	    user.getEmail(),
	        	    user.getName(),        // ← add this
	        	    saved.getTotalPrice().doubleValue()
	        	);
	        
	        System.out.println("Publishing order event: " + event);
	        orderEventProducer.publishOrderCreated(event);

	        return mapToResponse(saved);

	    }
	    public List<OrderResponse> getOrdersByUser(Long userId) {
	        return orderRepository.findByUserId(userId)
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

    public OrderResponse updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);

    }

    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        stats.put("totalOrders", orderRepository.count());
        stats.put("todayOrders", orderRepository.countOrdersCreatedToday(startOfDay, endOfDay));
        stats.put("totalRevenue", orderRepository.getTotalRevenue());
        stats.put("todayRevenue", orderRepository.getTodayRevenue(startOfDay, endOfDay));
        stats.put("pendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
        stats.put("processingOrders", orderRepository.countByStatus(OrderStatus.PROCESSING));
        stats.put("shippedOrders", orderRepository.countByStatus(OrderStatus.SHIPPED));
        stats.put("deliveredOrders", orderRepository.countByStatus(OrderStatus.DELIVERED));

        return stats;
    }

    public List<Map<String, Object>> getDailyRevenue(int days) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            Double revenue = orderRepository.getRevenueByDate(startOfDay, endOfDay);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("revenue", revenue != null ? revenue : 0.0);
            dayData.put("day", date.getDayOfWeek().toString().substring(0, 3));

            result.add(dayData);
        }

        return result;
    }
    
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    public com.example.order.entity.Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}