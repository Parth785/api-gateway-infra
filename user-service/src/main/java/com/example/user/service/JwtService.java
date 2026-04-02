package com.example.user.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtService {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole() != null ? user.getRole() : "USER")
                .claim("userName", user.getName())  // ← add this
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isAdmin(Claims claims) {
        return "ADMIN".equals(claims.get("role", String.class));
    }
    
 // generates admin JWT with ADMIN role claim
    public String generateAdminToken() {
        return Jwts.builder()
                .claim("userId", 0L)
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}