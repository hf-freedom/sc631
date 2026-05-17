package com.vehicle.entity;

public enum UrgencyLevel {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低");

    private final String description;

    UrgencyLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
