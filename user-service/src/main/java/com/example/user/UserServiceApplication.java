package com.example.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;


@SpringBootApplication
public class UserServiceApplication {
	
	@Value("${server.port}")
	private String port;

	@PostConstruct
	public void printPort() {
	    System.out.println("SERVER PORT FROM CONFIG: " + port);
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
