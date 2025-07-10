package com.example.Tech.services;

import com.example.Tech.dtos.CartDTO;
import com.example.Tech.dtos.CartItemDTO;
import com.example.Tech.entities.*;
import com.example.Tech.repos.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, UserRepo userRepo, ProductRepo productRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public CartDTO getCartByUsername(String username) {
        User user = findUser(username);
        Cart cart = getOrCreateCart(user);
        return CartDTO.fromEntity(cart);
    }

    public CartDTO addItemToCart(String username, CartItemDTO itemDto) {
        User user = findUser(username);
        Cart cart = getOrCreateCart(user);
        Product product = findProduct(itemDto.getProductId());

        // Check if item already exists in cart
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(itemDto.getQuantity());
            cart.getItems().add(newItem);
        }

        cartRepo.save(cart);
        return CartDTO.fromEntity(cart);
    }

    public CartDTO updateItemQuantity(String username, CartItemDTO itemDto) {
        User user = findUser(username);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(itemDto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(itemDto.getQuantity());
        cartRepo.save(cart);
        return CartDTO.fromEntity(cart);
    }

    public CartDTO removeItemFromCart(String username, Long itemId) {
        User user = findUser(username);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(item);
        cartItemRepo.delete(item);

        return CartDTO.fromEntity(cart);
    }

    public void clearCartByUsername(String username) {
        User user = findUser(username);
        cartItemRepo.deleteAllByCart_User_Id(user.getId());
    }

    public CartDTO mergeGuestCart(String username, List<CartItemDTO> guestItems) {
        User user = findUser(username);
        Cart cart = getOrCreateCart(user);

        for (CartItemDTO dto : guestItems) {
            Product product = findProduct(dto.getProductId());

            Optional<CartItem> existing = cart.getItems().stream()
                    .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existing.isPresent()) {
                CartItem item = existing.get();
                item.setQuantity(item.getQuantity() + dto.getQuantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(dto.getQuantity());
                cart.getItems().add(newItem);
            }
        }

        cartRepo.save(cart);
        return CartDTO.fromEntity(cart);
    }

    // Helpers
    private User findUser(String firebaseUid) {
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    private Product findProduct(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepo.save(cart);
                });
    }
}
