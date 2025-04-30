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
import java.util.UUID;

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

    // Create warranty for an order item
    public Warranty createWarranty(Long orderItemId, WarrantyRequestDTO warrantyDTO) {
        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);


        Warranty warranty = new Warranty();
        warranty.setDurationMonths(12);
        warranty.setStartDate(LocalDate.now());
        warranty.setOrderItem(orderItem);

        Warranty savedWarranty = warrantyRepo.save(warranty);
        sendWarrantyConfirmationEmail(savedWarranty);
        return savedWarranty;
    }

    // Get warranty by ID
    public Warranty getWarrantyById(Long id) {
        return warrantyRepo.findById(id)
                .orElseThrow(() -> null);
    }

    // Get warranty by order item
    public Warranty getWarrantyByOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
        return warrantyRepo.findByOrderItem(orderItem)
                .orElseThrow(() -> null);
    }

    // Extend existing warranty
    public Warranty extendWarranty(Long warrantyId, int additionalMonths) {
        if (additionalMonths <= 0) {
            throw new IllegalArgumentException("Additional months must be positive");
        }

        Warranty warranty = getWarrantyById(warrantyId);
        warranty.setDurationMonths(warranty.getDurationMonths() + additionalMonths);
        return warrantyRepo.save(warranty);
    }

    // Check warranty status
    public WarrantyStatusResponse checkWarrantyStatus(Long warrantyId) {
        Warranty warranty = getWarrantyById(warrantyId);
        LocalDate endDate = warranty.getStartDate().plusMonths(warranty.getDurationMonths());
        boolean isValid = LocalDate.now().isBefore(endDate);

        return new WarrantyStatusResponse(
                warranty.getId(),
                warranty.getStartDate(),
                endDate,
                isValid,
                warranty.getOrderItem().getProduct().getName()
        );
    }




        // Notify custom


    private void sendWarrantyConfirmationEmail(Warranty warranty) {
        String customerEmail = warranty.getOrderItem().getOrder().getUser().getEmail();
        String productName = warranty.getOrderItem().getProduct().getName();
        LocalDate endDate = warranty.getStartDate().plusMonths(warranty.getDurationMonths());


    }
}
