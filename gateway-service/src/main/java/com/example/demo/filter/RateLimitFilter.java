package com.example.demo.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
    	
    	String path = request.getRequestURI();

        // 1️⃣ EXCLUDE these endpoints from rate limiting
        if (path.startsWith("/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }
    	System.out.println("RateLimitFilter executed for: " + request.getRequestURI());

        // 1️⃣ Get client IP
        String clientIp = request.getRemoteAddr();

        // Normalize localhost (same logic you already had)
        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
            clientIp = "localhost";
        }

        // 2️⃣ Check rate limit
        boolean allowed = rateLimiterService.isAllowed(clientIp);

        if (!allowed) {
            // 3️⃣ Block request
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return; // VERY IMPORTANT: stop the request here
        }

        // 4️⃣ Continue to controller
        filterChain.doFilter(request, response);
    }
}
