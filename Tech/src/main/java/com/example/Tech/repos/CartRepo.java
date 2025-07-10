package com.example.Tech.repos;

import com.example.Tech.entities.Cart;
import com.example.Tech.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    // Find a cart by the user
    Optional<Cart> findByUser(User user);
}
