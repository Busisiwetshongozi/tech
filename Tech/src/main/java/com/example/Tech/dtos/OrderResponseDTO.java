package com.example.Tech.dtos;

import java.math.BigDecimal;

public class OrderResponseDTO {
    private Long id;
    private String status;
    private BigDecimal total;

    // Constructor
    public OrderResponseDTO(Long id, String status, BigDecimal total) {
        this.id = id;
        this.status = status;
        this.total = total;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for total
    public BigDecimal getTotal() {
        return total;
    }

    // Setter for total
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
