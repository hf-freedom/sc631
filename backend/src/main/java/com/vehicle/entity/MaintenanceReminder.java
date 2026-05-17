package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceReminder {
    private String id;
    private String vehicleId;
    private String plateNumber;
    private Integer currentMileage;
    private Integer dueMileage;
    private Boolean handled;
    private LocalDateTime createdAt;
}
