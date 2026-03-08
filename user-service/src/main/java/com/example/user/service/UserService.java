package com.example.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.User;
import com.example.user.exception.EmailAlreadyExistsException;
import com.example.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	 private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final JwtService jwtService;
     
     public String login(LoginRequest request) {

    	    User user = userRepository.findByEmail(request.getEmail())
    	            .orElseThrow(() -> new RuntimeException("User not found"));

    	    if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    	        throw new RuntimeException("Invalid credentials");
    	    }

    	    return jwtService.generateToken(user);
    	}

     public UserResponse createUser(CreateUserRequest request) {

    	    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	        throw new EmailAlreadyExistsException("Email already exists");
    	    }

    	    User user = new User();
    	    user.setName(request.getName());
    	    user.setEmail(request.getEmail());
    	    user.setPassword(passwordEncoder.encode(request.getPassword()));

    	    User saved = userRepository.save(user);

    	    return new UserResponse(
    	        saved.getId(),
    	        saved.getName(),
    	        saved.getEmail(),
    	        saved.getCreatedAt()
    	    );
    	}
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}