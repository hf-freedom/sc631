package com.vehicle.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Vehicle {
    private String id;
    private String plateNumber;
    private String brand;
    private String model;
    private Integer mileage;
    private Integer lastMaintenanceMileage;
    private Integer maintenanceCycle;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
