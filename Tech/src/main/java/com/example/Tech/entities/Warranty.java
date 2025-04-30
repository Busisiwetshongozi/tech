package com.example.Tech.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int durationMonths;   // e.g., 3, 6, 12
    private LocalDate startDate;

    @OneToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for durationMonths
    public int getDurationMonths() {
        return durationMonths;
    }

    // Setter for durationMonths
    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    // Getter for startDate
    public LocalDate getStartDate() {
        return startDate;
    }

    // Setter for startDate
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Getter for orderItem
    public OrderItem getOrderItem() {
        return orderItem;
    }

    // Setter for orderItem
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
