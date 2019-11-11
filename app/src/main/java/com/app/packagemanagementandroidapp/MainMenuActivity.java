package com.app.packagemanagementandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Menu główne");
    }

    public void packageScan(View view){
        startActivity(new Intent(getApplicationContext(), PackageInfoActivity.class));

    }

    public void scanCar(View view) {
        startActivity(new Intent(getApplicationContext(), CarInfoActivity.class));
    }

    public void productScan(View view) {
        startActivity(new Intent(getApplicationContext(), ProductInfoActivity.class));
    }

    public void warehouseScan(View view) {
        startActivity(new Intent(getApplicationContext(), WarehouseInfoActivity.class));
    }
}
