package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();

        System.out.println("[Gateway Request] " + method + " " + path);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    int statusCode = exchange.getResponse().getStatusCode().value();
                    System.out.println("[Gateway Response] Status Code: " + statusCode);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}