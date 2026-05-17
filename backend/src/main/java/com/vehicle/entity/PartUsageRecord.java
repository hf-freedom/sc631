package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PartUsageRecord {
    private String id;
    private String repairOrderId;
    private String partId;
    private String partName;
    private Integer quantity;
    private LocalDateTime createdAt;
}
