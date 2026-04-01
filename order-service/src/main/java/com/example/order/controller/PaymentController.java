package com.example.order.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.dto.PaymentVerificationRequest;
import com.example.order.dto.OrderStatus;
import com.example.order.service.OrderService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    // Step 1 — create Razorpay order
    @PostMapping("/{orderId}/payment/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @PathVariable Long orderId) {
        try {
            // get order from DB to get total price
            var order = orderService.getOrderById(orderId);

            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject options = new JSONObject();
            options.put("amount", (int)(order.getTotalPrice().doubleValue() * 100)); // paise
            options.put("currency", "INR");
            options.put("receipt", "order_" + orderId);

            Order razorpayOrder = client.orders.create(options);

            return ResponseEntity.ok(Map.of(
                "razorpayOrderId", razorpayOrder.get("id"),
                "amount", razorpayOrder.get("amount"),
                "currency", razorpayOrder.get("currency"),
                "keyId", keyId,
                "orderId", orderId
            ));

        } catch (Exception e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Payment creation failed"));
        }
    }

    // Step 2 — verify payment after user pays
    @PostMapping("/{orderId}/payment/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentVerificationRequest request) {
        try {
            // verify signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid payment signature"));
            }

            // update order status to PROCESSING
            orderService.updateStatus(orderId, OrderStatus.PROCESSING);

            log.info("Payment verified for order #{}", orderId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful",
                "orderId", orderId,
                "paymentId", request.getRazorpayPaymentId()
            ));

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Verification failed"));
        }
    }
}