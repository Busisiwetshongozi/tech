package com.example.Tech.services;

import com.example.Tech.dtos.UserRegistrationDTO;
import com.example.Tech.entities.Order;
import com.example.Tech.entities.User;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.repos.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepo orderRepo;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, OrderRepo orderRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.orderRepo = orderRepo;
    }

    // UserDetailsService implementation
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // User management
    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User registerUser(User user) throws Exception {
        // Check if the user already exists by Firebase UID
        if (userRepo.existsByFirebaseUid(user.getFirebaseUid())) {
            throw new Exception("User already exists");
        }

        // Log for debugging
        System.out.println("Saving user with name: " + user.getName());

        // Save user to the database
        return userRepo.save(user);
    }

    public boolean userExists(Long uid) {
        return userRepo.existsById(uid);
    }

    public User findByUid(Long uid) {
        return userRepo.findById(uid).orElse(null);
    }

    public void disableUser(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(false);
        userRepo.save(user);
    }

    // Admin functions
    public User authenticate(String email, String rawPassword) {
        // 1. Find user by email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 2. Verify password
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.warn("Failed login attempt for user: {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }

        // 3. Check if account is active
        if (!user.isEnabled()) {
            logger.warn("Disabled account login attempt: {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }

        logger.info("User logged in: {}", email);
        return user; // Return authenticated user (without password)
    }

    // This method is where you are registering the user after Firebase Authentication
    public User findOrCreateUserFromFirebase(String firebaseUid, String email, String name, String address, String phone) {
        // Check if the user already exists by Firebase UID
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    // If user doesn't exist, create a new one
                    User newUser = new User();
                    newUser.setFirebaseUid(firebaseUid);  // Firebase UID
                    newUser.setEmail(email);  // Email from Firebase
                    newUser.setName(name);  // Name from the request body
                    newUser.setAddress(address);  // Custom address
                    newUser.setPhone(phone);  // Custom phone number
                    newUser.setEnabled(true);  // Enable the account
                    return userRepo.save(newUser);  // Save new user to the database
                });
    }

    public User getUserByFirebaseUid(String firebaseUid) {
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found with Firebase UID: " + firebaseUid));
    }
}
