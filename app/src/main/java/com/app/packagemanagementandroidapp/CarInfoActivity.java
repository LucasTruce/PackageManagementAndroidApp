package com.app.packagemanagementandroidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.packagemanagementandroidapp.dialog.CarStatusDialog;
import com.app.packagemanagementandroidapp.dialog.PackageStatusDialog;
import com.app.packagemanagementandroidapp.model.Car;
import com.app.packagemanagementandroidapp.service.CarService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarInfoActivity extends AppCompatActivity {

    TextView carBrand;
    TextView carModel;
    TextView carPower;
    TextView carCapacity;
    TextView carColor;
    TextView carStatus;
    TextView carType;
    TextView carEngineType;
    TextView carLicensePlate;
    TextView carLoad;

    SwipeRefreshLayout refreshLayout;

    Car car;
    CarService carService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        carService = ServiceGenerator.createService(CarService.class, sharedPref.getString("TOKEN", ""));

        carBrand = findViewById(R.id.carBrand);
        carModel = findViewById(R.id.carModel);
        carPower = findViewById(R.id.carPower);
        carLoad = findViewById(R.id.carLoad);
        carColor = findViewById(R.id.carColor);
        carStatus = findViewById(R.id.carStatus);
        carType = findViewById(R.id.carType);
        carEngineType = findViewById(R.id.carEngineType);
        carLicensePlate = findViewById(R.id.carLicensePlate);
        carCapacity = findViewById(R.id.carEngineCapacity);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setPrompt("Zeskanuj kod QR samochodu");
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityHorizontal.class);
        integrator.initiateScan();

        refreshLayout = findViewById(R.id.swipeToRefreshCar);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGetCar(car.getId().toString()).execute();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.car_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.changeCarStatus:
                openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openDialog() {
        CarStatusDialog carStatusDialog = new CarStatusDialog(car);
        carStatusDialog.show(getSupportFragmentManager(), "StatusDialog");
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

                if(jobj.get("carId") != null) {
                    new AsyncGetCar(jobj.get("carId").toString()).execute();
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

    public void getCar(String carId) {
        Call<Car> call = carService.getCar(carId);

        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if(response.isSuccessful()){
                    car = response.body();
                    carBrand.setText(car.getBrand());
                    carLoad.setText(String.valueOf(car.getLoad()));
                    carModel.setText(car.getModel());
                    carColor.setText(car.getColor());
                    carLicensePlate.setText(car.getLicensePlate());
                    carEngineType.setText(car.getEngineType());
                    carPower.setText(String.valueOf(car.getPower()));
                    carType.setText(car.getType());
                    carStatus.setText(car.getCarStatus().getName());
                    carCapacity.setText(String.valueOf(car.getCapacity()));

                }
                else if(response.code() == 401){
                    Toast.makeText(getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {

            }
        });
    }

    public class AsyncGetCar extends AsyncTask<Void, Void, Void> {
        String carId;

        public AsyncGetCar(String carId) {
            this.carId = carId;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(carId != null)
                getCar(carId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           this.carId = null;
            super.onPostExecute(aVoid);
        }
    }
}
