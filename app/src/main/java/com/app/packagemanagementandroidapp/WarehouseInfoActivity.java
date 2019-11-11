package com.app.packagemanagementandroidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.packagemanagementandroidapp.model.Warehouse;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.app.packagemanagementandroidapp.service.WarehouseService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WarehouseInfoActivity extends AppCompatActivity {

    TextView warehouseDescription;
    TextView warehouseCity;
    TextView warehousePostCode;
    TextView warehouseStreet;
    TextView warehousePhoneNumber;

    Warehouse warehouse;
    WarehouseService warehouseService;

    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_info);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Informacje o magazynie");

        warehouseDescription = findViewById(R.id.warehouseDescription);
        warehouseCity = findViewById(R.id.warehouseCity);
        warehousePostCode = findViewById(R.id.warehousePostCode);
        warehouseStreet = findViewById(R.id.warehouseStreet);
        warehousePhoneNumber = findViewById(R.id.warehousePhoneNumber);

        refreshLayout = findViewById(R.id.swipeToRefreshWarehouse);

        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        warehouseService = ServiceGenerator.createService(WarehouseService.class, sharedPref.getString("TOKEN", ""));

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setPrompt("Zeskanuj kod QR magazynu");
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityHorizontal.class);
        integrator.initiateScan();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                Toast.makeText(getApplicationContext(), "Skanowanie zostało anulowane", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                JsonObject jobj = new Gson().fromJson(result.getContents(), JsonObject.class);

                if(jobj.get("warehouseId") != null) {
                    new AsyncGetWarehouse(jobj.get("warehouseId").toString()).execute();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                    Toast.makeText(getApplicationContext(), "Zeskanuj prawidłowy kod QR!", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getWarehouse(String warehouseId) {
        Call<Warehouse> call = warehouseService.getWarehouse(warehouseId);

        call.enqueue(new Callback<Warehouse>() {
            @Override
            public void onResponse(Call<Warehouse> call, Response<Warehouse> response) {
               if(response.isSuccessful()) {
                   warehouse = response.body();

                   warehouseDescription.setText(warehouse.getDescription());
                   warehouseCity.setText(warehouse.getCity());
                   warehousePostCode.setText(warehouse.getPostCode());
                   warehouseStreet.setText(warehouse.getStreet());
                   warehousePhoneNumber.setText(warehouse.getPhoneNumber());
               }
               else if(response.code() == 401)
                   Toast.makeText(getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Warehouse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class AsyncGetWarehouse extends AsyncTask<Void, Void, Void> {
        String warehouseId;


        public AsyncGetWarehouse(String warehouseId) {
            this.warehouseId = warehouseId;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(warehouseId != null)
                getWarehouse(warehouseId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.warehouseId = null;
            super.onPostExecute(aVoid);
        }
    }
}
