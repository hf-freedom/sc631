package com.vehicle.entity;

public enum RepairOrderStatus {
    PENDING("待开始"),
    IN_PROGRESS("进行中"),
    PAUSED("已暂停"),
    COMPLETED("已完成");

    private final String description;

    RepairOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
