package com.example.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.service.UserService;
import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
	    return ResponseEntity.status(201).body(userService.createUser(request));
	}

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable String id) {
    	//Testing purpose only, returning dummy data
//        Map<String, Object> user = new HashMap<>();
//        user.put("id", id);
//        user.put("name", "Dummy User");
//        user.put("service", "user-service");
//        return user;
		return userService.getUserById(Long.parseLong(id));
    }
}
