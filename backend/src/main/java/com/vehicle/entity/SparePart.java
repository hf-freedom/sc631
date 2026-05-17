package com.vehicle.entity;

import lombok.Data;

@Data
public class SparePart {
    private String id;
    private String name;
    private String model;
    private Integer stock;
    private Double unitPrice;
}
