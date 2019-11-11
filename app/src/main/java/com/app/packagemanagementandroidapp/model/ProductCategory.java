package com.app.packagemanagementandroidapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategory {
    private Long id;
    private String name;
    private String description;
    private List<Product> products;
}
