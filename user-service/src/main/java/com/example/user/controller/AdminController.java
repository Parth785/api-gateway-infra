package com.example.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.AdminLoginRequest;
import com.example.user.dto.AdminStatsResponse;
import com.example.user.repository.UserRepository;
import com.example.user.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JwtService jwtUtil;
    private final UserRepository userRepository;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    // POST /admin/login
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody AdminLoginRequest request) {

        if (!request.getUsername().equals(adminUsername) ||
            !request.getPassword().equals(adminPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin credentials");
        }

        // generate JWT with ADMIN role
        String token = jwtUtil.generateAdminToken();
        return ResponseEntity.ok(token);
    }

    // GET /admin/stats/users
    @GetMapping("/stats/users")
    public ResponseEntity<AdminStatsResponse> getUserStats() {
        Long totalUsers = userRepository.count();
        Long todayUsers = userRepository.countUsersCreatedToday();
        return ResponseEntity.ok(new AdminStatsResponse(totalUsers, todayUsers));
    }
}