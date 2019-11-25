package com.app.packagemanagementandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageResponse {

    private int number;
    private List<Pack> content;
    private int totalPages;
    @SerializedName("size")
    private int pageSize;
    private boolean last;


}
