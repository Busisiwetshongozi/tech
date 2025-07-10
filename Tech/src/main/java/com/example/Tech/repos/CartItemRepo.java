

package com.example.Tech.repos;

import com.example.Tech.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart_Id(Long cartId);

    @Transactional
    @Modifying
    void deleteAllByCart_User_Id(Long userId);

    List<CartItem> findByCart_User_Id(Long userId);
}
