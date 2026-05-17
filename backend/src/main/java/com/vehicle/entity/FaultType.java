package com.vehicle.entity;

public enum FaultType {
    ENGINE("发动机故障", UrgencyLevel.HIGH),
    BRAKE("制动系统故障", UrgencyLevel.HIGH),
    ELECTRICAL("电气系统故障", UrgencyLevel.MEDIUM),
    TIRE("轮胎故障", UrgencyLevel.MEDIUM),
    COOLING("冷却系统故障", UrgencyLevel.MEDIUM),
    SUSPENSION("悬挂系统故障", UrgencyLevel.MEDIUM),
    BODY("车身故障", UrgencyLevel.LOW),
    OTHER("其他故障", UrgencyLevel.LOW);

    private final String description;
    private final UrgencyLevel defaultUrgency;

    FaultType(String description, UrgencyLevel defaultUrgency) {
        this.description = description;
        this.defaultUrgency = defaultUrgency;
    }

    public String getDescription() {
        return description;
    }

    public UrgencyLevel getDefaultUrgency() {
        return defaultUrgency;
    }
}
