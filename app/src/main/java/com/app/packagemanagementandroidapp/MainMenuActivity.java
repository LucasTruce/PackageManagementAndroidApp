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
        bar.setTitle("Menu");
    }

    public void packageScan(View view){
        Intent intent = new Intent(getApplicationContext(), PackageInfoActivity.class);
        startActivity(intent);

    }

}
