package com.example.Tech.services;

import com.example.Tech.dtos.WarrantyRequestDTO;
import com.example.Tech.dtos.WarrantyStatusResponse;
import com.example.Tech.entities.OrderItem;
import com.example.Tech.entities.Warranty;
import com.example.Tech.repos.WarrantyRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Transactional
@Service
public class WarrantyService {

    private final WarrantyRepo warrantyRepo;
    private final OrderItemService orderItemService;


    @Autowired
    public WarrantyService(WarrantyRepo warrantyRepo,
                           OrderItemService orderItemService)
    {
        this.warrantyRepo = warrantyRepo;
        this.orderItemService = orderItemService;
    }

}
