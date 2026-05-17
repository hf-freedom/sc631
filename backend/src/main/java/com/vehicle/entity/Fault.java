package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Fault {
    private String id;
    private String vehicleId;
    private String plateNumber;
    private String driverId;
    private String driverName;
    private String description;
    private FaultType type;
    private UrgencyLevel urgencyLevel;
    private FaultStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
