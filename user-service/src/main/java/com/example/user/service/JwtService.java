package com.example.user.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.user.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtService {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    public String generateToken(User user) {

        return Jwts.builder()
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}