package com.vehicle.entity;

import lombok.Data;

@Data
public class Statistics {
    private Integer totalVehicles;
    private Integer availableVehicles;
    private Double availabilityRate;
    private Integer totalRepairs;
    private Integer overdueMaintenance;
    private Integer pendingFaults;
    private Integer highUrgencyFaults;
    private Integer mediumUrgencyFaults;
    private Integer lowUrgencyFaults;
}
