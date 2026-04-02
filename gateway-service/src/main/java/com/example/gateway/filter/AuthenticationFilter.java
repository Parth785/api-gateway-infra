package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor	
public class AuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    
    public AuthenticationFilter() {
    	this.jwtUtil = new JwtUtil();
        System.out.println("AuthenticationFilter loaded");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Incoming request path: " + path);
        // allow login endpoint
        if (path.startsWith("/users") ||
        	    path.equals("/admin/login") ||
        	    (path.startsWith("/products") && exchange.getRequest().getMethod().name().equals("GET")) ||
        	    (path.startsWith("/reviews") && exchange.getRequest().getMethod().name().equals("GET"))) {
        	    return chain.filter(exchange);
        	}

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class) != null ? claims.get("role", String.class) : "USER";
            String userName = claims.get("userName", String.class) != null ? claims.get("userName", String.class) : "User";

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId != null ? userId.toString() : "0")
                .header("X-User-Role", role)
                .header("X-User-Name", userName)
                .build();
            
//            String role = claims.get("role", String.class) != null
//            	    ? claims.get("role", String.class)
//            	    : "USER";
//
//            ServerHttpRequest mutatedRequest = exchange.getRequest()
//            	    .mutate()
//            	    .header("X-User-Id", userId.toString())
//            	    .header("X-User-Role", role)
//            	    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}