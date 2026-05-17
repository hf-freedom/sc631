package com.vehicle.entity;

public enum FaultStatus {
    PENDING("待处理"),
    ASSIGNED("已派单"),
    IN_PROGRESS("处理中"),
    PAUSED("已暂停"),
    COMPLETED("已完成");

    private final String description;

    FaultStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
