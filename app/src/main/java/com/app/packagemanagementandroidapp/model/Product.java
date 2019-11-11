package com.app.packagemanagementandroidapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Long id;
    private String name;
    private String comments;
    private float weight;

    private Code code;
    private Content content;
    private ProductCategory category;
}
