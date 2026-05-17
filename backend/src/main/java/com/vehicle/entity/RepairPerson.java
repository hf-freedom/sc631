package com.vehicle.entity;

import lombok.Data;

@Data
public class RepairPerson {
    private String id;
    private String name;
    private String phone;
    private String specialty;
    private Boolean available;
}
