package com.vehicle.entity;

import lombok.Data;

@Data
public class RepairStation {
    private String id;
    private String name;
    private String location;
    private Boolean available;
}
