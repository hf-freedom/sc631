package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseRequest {
    private String id;
    private String repairOrderId;
    private String partId;
    private String partName;
    private Integer requiredQuantity;
    private PurchaseRequestStatus status;
    private LocalDateTime createdAt;
}
