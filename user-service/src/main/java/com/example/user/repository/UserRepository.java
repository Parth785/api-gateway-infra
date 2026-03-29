package com.example.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM users u WHERE DATE(u.created_at) = CURRENT_DATE", nativeQuery = true)
    Long countUsersCreatedToday();
    
}