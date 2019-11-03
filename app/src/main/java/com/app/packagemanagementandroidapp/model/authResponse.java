package com.app.packagemanagementandroidapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class authResponse {

    private List<Role> roles;
    private String jwttoken;
}
