package com.example.Tech.repos;

import com.example.Tech.entities.Order;
import com.example.Tech.entities.User;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);

    Optional<Order> findByIdAndUser(Long id, User user);
    List<Order> findByUser_FirebaseUid(String firebaseUid);

}
