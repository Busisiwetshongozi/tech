package com.example.Tech.repos;

import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface WarrantyRepo extends JpaRepository<Warranty,Long> {
    Optional<Warranty> findByOrderItem(OrderItem orderItem);
    boolean existsByOrderItem(OrderItem orderItem);
}
