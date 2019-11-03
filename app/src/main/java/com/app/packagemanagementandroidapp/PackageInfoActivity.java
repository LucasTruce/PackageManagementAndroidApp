package com.app.packagemanagementandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.packagemanagementandroidapp.model.Pack;

import com.app.packagemanagementandroidapp.service.PackageService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.text.DateFormat;
import java.text.SimpleDateFormat;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PackageInfoActivity extends AppCompatActivity {

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://192.168.0.2:8080/")
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit = builder.build();

    PackageService packageService = retrofit.create(PackageService.class);
    TextView packageNumber;
    TextView packageStatus;
    TextView packageSendDate;
    TextView packageSize;

    TextView senderName;
    TextView senderEmail;
    TextView senderCompany;
    TextView senderCity;
    TextView senderStreet;
    TextView senderPhoneNumber;

    TextView receiverName;
    TextView receiverEmail;
    TextView receiverCompany;
    TextView receiverCity;
    TextView receiverStreet;
    TextView receiverPhoneNumber;

    TextView warehouseCity;
    TextView warehouseStreet;
    TextView warehousePostCode;
    TextView warehousePhoneNumber;
    TextView warehouseDescription;

    TextView carBrand;
    TextView carModel;
    TextView carLicensePlate;
    TextView carCapacity;
    TextView carStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_info);
        getSupportActionBar().setTitle("Informacje o paczce");

        packageNumber = findViewById(R.id.packageNumberView);
        packageStatus = findViewById(R.id.packageStatusView);
        packageSendDate= findViewById(R.id.packageSendDateView);
        packageSize = findViewById(R.id.packageSize);

        senderName = findViewById(R.id.packageSenderName);
        senderEmail = findViewById(R.id.packageSenderEmail);
        senderCompany = findViewById(R.id.packageSenderCompany);
        senderCity = findViewById(R.id.packageSenderCity);
        senderStreet = findViewById(R.id.packageSenderStreet);
        senderPhoneNumber  = findViewById(R.id.packageSenderPhoneNumber);

        receiverName = findViewById(R.id.packageReceiverName);
        receiverEmail = findViewById(R.id.packageReceiverEmail);
        receiverCompany = findViewById(R.id.packageReceiverCompany);
        receiverCity = findViewById(R.id.packageReceiverCity);
        receiverStreet = findViewById(R.id.packageReceiverStreet);
        receiverPhoneNumber = findViewById(R.id.packageReceiverPhoneNumber);

        warehouseCity = findViewById(R.id.packageWarehouseCity);
        warehouseStreet = findViewById(R.id.packageWarehouseStreet);
        warehousePostCode = findViewById(R.id.packageWarehousePostalCode);
        warehousePhoneNumber = findViewById(R.id.packageWarehousePhoneNumber);
        warehouseDescription = findViewById(R.id.packageWarehouseDescription);

        carBrand = findViewById(R.id.packageCarBrand);
        carModel = findViewById(R.id.packageCarModel);
        carLicensePlate = findViewById(R.id.packageCarLicensePlate);
        carCapacity = findViewById(R.id.packageCarCapacity);
        carStatus = findViewById(R.id.packageCarStatus);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityHorizontal.class);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                JsonObject jobj = new Gson().fromJson(result.getContents(), JsonObject.class);

                new AsyncGetPackage(this, "" + jobj.get("packId")).execute();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getPackage(String packId) {
        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        Call<Pack> call = packageService.getPack(packId, sharedPref.getString("TOKEN", ""));

        call.enqueue(new Callback<Pack>() {

            @Override
            public void onResponse(Call<Pack> call, Response<Pack> response) {
                if(response.isSuccessful()){        //gdy dane zostały prawidłowo pobrane z serwera ustawiamy odpowiednie pola w aktywności
                    Pack pack = response.body();
                    Toast.makeText(getApplicationContext(), "OPERACJA UDANA", Toast.LENGTH_SHORT).show();
                    packageNumber.setText(pack.getPackageNumber());
                    packageSendDate.setText(pack.getDate().split("T")[0]);
                    packageSize.setText(pack.getHeight() + "x" + pack.getLength() + "x" + pack.getWidth());

                    packageStatus.setText(pack.getPackageStatus().getName());

                    senderName.setText(pack.getSender().getName() + " " + pack.getSender().getLastName());
                    senderEmail.setText(pack.getSender().getEmail());
                    senderCompany.setText(pack.getSender().getCompanyName());
                    senderCity.setText(pack.getSender().getCity());
                    senderStreet.setText(pack.getSender().getPostCode() + ", " + pack.getSender().getStreet());
                    senderPhoneNumber.setText(pack.getSender().getPhoneNumber());

                    receiverName.setText(pack.getRecipient().getName() + " " + pack.getSender().getLastName());
                    receiverEmail.setText(pack.getRecipient().getEmail());
                    receiverCompany.setText(pack.getRecipient().getCompanyName());
                    receiverCity.setText(pack.getRecipient().getCity());
                    receiverStreet.setText(pack.getRecipient().getPostCode() + "," + pack.getSender().getStreet());
                    receiverPhoneNumber.setText(pack.getRecipient().getPhoneNumber());

                    carBrand.setText(pack.getCar().getBrand());
                    carModel.setText(pack.getCar().getModel());
                    carCapacity.setText(Double.toString(pack.getCar().getCapacity()));
                    carLicensePlate.setText(pack.getCar().getLicensePlate());
                    carStatus.setText(pack.getCar().getCarStatus().getName());

                    warehouseCity.setText(pack.getWarehouses().get(0).getCity());
                    warehousePhoneNumber.setText(pack.getWarehouses().get(0).getPhoneNumber());
                    warehousePostCode.setText(pack.getWarehouses().get(0).getPostCode());
                    warehouseStreet.setText(pack.getWarehouses().get(0).getStreet());
                    warehouseDescription.setText(pack.getWarehouses().get(0).getDescription());
                }
                else{
                    if(response.code() == 401)
                        Toast.makeText(getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Pack> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class AsyncGetPackage extends AsyncTask<Void, Void, Void> {
        Activity packageInfoActivity;
        String packId;

        public AsyncGetPackage(Activity packageInfoActivity, String packId) {
            this.packageInfoActivity = packageInfoActivity;
            this.packId = packId;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            getPackage(packId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}

