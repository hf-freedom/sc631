package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepairOrder {
    private String id;
    private String faultId;
    private String vehicleId;
    private String plateNumber;
    private String repairPersonId;
    private String repairPersonName;
    private String stationId;
    private String stationName;
    private RepairOrderStatus status;
    private List<String> requiredParts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
