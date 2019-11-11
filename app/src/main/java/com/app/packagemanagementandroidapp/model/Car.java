package com.app.packagemanagementandroidapp.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
    private Long id;

    private String brand;
    private String model;
    private String licensePlate;
    private String color;
    private String engineType;
    private String type;
    private String comments;

    private int power;
    private double capacity;
    private float load;

    private Code code;
    private CarStatus carStatus;

}
