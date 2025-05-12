package com.example.Tech.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class OrderRequest {
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<OrderItemDTO> items;

    // Getters & Setters
    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
