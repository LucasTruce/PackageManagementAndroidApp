package com.app.packagemanagementandroidapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pack {
    private Long id;
    private String packageNumber;
    private String date;
    private String comments;

    private int length;
    private int height;
    private int width;

    private PackageStatus packageStatus;
    private Recipient recipient;
    private Sender sender;
    private List<Warehouse> warehouses;
    private Car car;

    private Content content;
    private Code code;

}
