package com.example.Tech.dtos;

import java.time.LocalDate;

public class WarrantyStatusResponse {
    private Long warrantyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean valid;
    private String productName;
    public WarrantyStatusResponse(Long warrantyId, LocalDate startDate,
                                  LocalDate endDate, boolean valid, String productName) {
        this.warrantyId = warrantyId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.valid = valid;
        this.productName = productName;
    }
}
