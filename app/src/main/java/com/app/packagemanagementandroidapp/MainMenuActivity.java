package com.app.packagemanagementandroidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Menu główne");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences sharedPref = getSharedPreferences("pref", 0);
                sharedPref.edit().clear().apply();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Zostałeś poprawnie wylogowany!", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void findPackagesForDelivery(View view) {
        startActivity(new Intent(getApplicationContext(), PaginationPackageActivity.class));
    }
}
