package com.example.Tech.dtos;

import com.example.Tech.entities.Cart;

import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    // Constructors
    public CartDTO() {}

    public CartDTO(Long id, List<CartItemDTO> items) {
        this.id = id;
        this.items = items;
    }

    // Convert from Cart entity to DTO
    public static CartDTO fromEntity(Cart cart) {
        List<CartItemDTO> itemDtos = cart.getItems().stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList());

        return new CartDTO(cart.getId(), itemDtos);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}
