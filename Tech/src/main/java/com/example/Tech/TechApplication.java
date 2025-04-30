package com.example.Tech;

import com.example.Tech.config.FirebaseInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@SpringBootApplication
public class TechApplication implements CommandLineRunner { // Implement CommandLineRunner

	public static void main(String[] args) {
		SpringApplication.run(TechApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Override the run method from CommandLineRunner interface
	@Override
	public void run(String... args) throws Exception {
		try {
			// Initialize Firebase at the start of the application
			FirebaseInitializer.initialize();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error initializing Firebase");
		}
	}
}

