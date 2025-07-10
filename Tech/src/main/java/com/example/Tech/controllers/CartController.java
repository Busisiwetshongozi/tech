package com.example.Tech.controllers;

import com.example.Tech.dtos.CartDTO;
import com.example.Tech.dtos.CartItemDTO;
import com.example.Tech.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Get the current user's cart
    @GetMapping
    public ResponseEntity<CartDTO> getCart(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItem(@RequestBody CartItemDTO itemDto, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.addItemToCart(username, itemDto));
    }

    // Update item quantity
    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateItem(@RequestBody CartItemDTO itemDto, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.updateItemQuantity(username, itemDto));
    }

    // Remove item from cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long itemId, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.removeItemFromCart(username, itemId));
    }

    // Clear cart
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        String username = principal.getName();
        cartService.clearCartByUsername(username);
        return ResponseEntity.ok().build();
    }

    // Merge guest cart after login
    @PostMapping("/merge")
    public ResponseEntity<CartDTO> mergeCart(@RequestBody List<CartItemDTO> guestItems, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.mergeGuestCart(username, guestItems));
    }
}
