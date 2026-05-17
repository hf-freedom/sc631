package com.vehicle.entity;

public enum VehicleStatus {
    AVAILABLE("可用"),
    IN_MAINTENANCE("保养中"),
    IN_REPAIR("维修中"),
    OUT_OF_SERVICE("停用");

    private final String description;

    VehicleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
