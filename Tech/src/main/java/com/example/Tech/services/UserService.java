package com.example.Tech.services;
import com.example.Tech.enums.Role;
import com.example.Tech.entities.User;
import com.example.Tech.repos.OrderRepo;
import com.example.Tech.repos.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        // Set default role to CUSTOMER if not specified
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        logger.info("Registering new user with email: {}", user.getEmail());
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
    @PreAuthorize("hasRole('ADMIN')")
    public User promoteToAdmin(Long userId) {
        User user = getUserById(userId);
        user.setRole(Role.ADMIN);
        return userRepo.save(user);
    }

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
        return user;
    }

    public User findOrCreateUserFromFirebase(String firebaseUid, String email, String name, String address, String phone) {
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setFirebaseUid(firebaseUid);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setAddress(address);
                    newUser.setPhone(phone);
                    newUser.setEnabled(true);
                    newUser.setRole(Role.CUSTOMER); // Set default role for Firebase users
                    return userRepo.save(newUser);
                });
    }

    public User getUserByFirebaseUid(String firebaseUid) {
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found with Firebase UID: " + firebaseUid));
    }

    // Additional role-based methods
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        userRepo.deleteById(userId);
    }
}