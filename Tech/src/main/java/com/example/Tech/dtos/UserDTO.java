package com.example.Tech.dtos;

import com.example.Tech.entities.User;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;

    // Manual Getter for `id`
    public Long getId() {
        return this.id;
    }

    // Manual Setter for `id`
    public void setId(Long id) {
        this.id = id;
    }

    // Manual Getter for `name`
    public String getName() {
        return this.name;
    }

    // Manual Setter for `name`
    public void setName(String name) {
        this.name = name;
    }

    // Manual Getter for `email`
    public String getEmail() {
        return this.email;
    }

    // Manual Setter for `email`
    public void setEmail(String email) {
        this.email = email;
    }

    // Manual Getter for `role`
    public String getRole() {
        return this.role;
    }

    // Manual Setter for `role`
    public void setRole(String role) {
        this.role = role;
    }

    // Static conversion method (unchanged)
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        return dto;
    }
}
