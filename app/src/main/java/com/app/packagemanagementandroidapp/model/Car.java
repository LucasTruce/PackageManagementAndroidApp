package com.app.packagemanagementandroidapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private double capacity;
    private CarStatus carStatus;
}
