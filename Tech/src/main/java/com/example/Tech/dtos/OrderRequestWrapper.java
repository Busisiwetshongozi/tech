package com.example.Tech.dtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderRequestWrapper {
    private OrderItemDTO item;              // Single item
    private List<OrderItemDTO> items;       // Multiple items

    public OrderItemDTO getItem() {
        return item;
    }

    public void setItem(OrderItemDTO item) {
        this.item = item;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    /**
     * Returns a unified list of order items, regardless of whether
     * 'item' or 'items' was provided.
     */
    public List<OrderItemDTO> getUnifiedItemList() {
        if (items != null && !items.isEmpty()) {
            return items;
        } else if (item != null) {
            return Collections.singletonList(item);
        } else {
            return new ArrayList<>();
        }
    }
}
