package com.vehicle.entity;

public enum PurchaseRequestStatus {
    PENDING("待采购"),
    PURCHASING("采购中"),
    COMPLETED("已完成");

    private final String description;

    PurchaseRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
