package com.app.packagemanagementandroidapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recipient {
    private String name;
    private String lastName;
    private String companyName;
    private String city;
    private String street;
    private String postCode;
    private String phoneNumber;
    private String email;
    private Long id;
}
