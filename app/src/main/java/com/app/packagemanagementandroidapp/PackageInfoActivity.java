package com.app.packagemanagementandroidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.packagemanagementandroidapp.dialog.PackageStatusDialog;
import com.app.packagemanagementandroidapp.model.Car;
import com.app.packagemanagementandroidapp.model.Pack;

import com.app.packagemanagementandroidapp.model.Warehouse;
import com.app.packagemanagementandroidapp.service.CarService;
import com.app.packagemanagementandroidapp.service.PackageService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.app.packagemanagementandroidapp.service.WarehouseService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageInfoActivity extends AppCompatActivity {

    PackageService packageService;
    CarService carService;
    WarehouseService warehouseService;

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

    Pack pack;

    boolean carScan = false;
    boolean warehouseScan = false;
    boolean packScan = false;

    SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_info);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Informacje o paczce");

        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        packageService = ServiceGenerator.createService(PackageService.class, sharedPref.getString("TOKEN", ""));
        carService = ServiceGenerator.createService(CarService.class, sharedPref.getString("TOKEN", ""));
        warehouseService = ServiceGenerator.createService(WarehouseService.class, sharedPref.getString("TOKEN", ""));

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

        refreshLayout = findViewById(R.id.swipeToRefresh);

        packScan = true;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setPrompt("Zeskanuj kod QR paczki");
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityHorizontal.class);
        integrator.initiateScan();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGetPackage("" + pack.getId(), null, null).execute();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.package_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityHorizontal.class);

        switch (item.getItemId()) {
            case R.id.changeStatus:
                openDialog();
                return true;
            case R.id.changeCar:
                carScan = true;
                integrator.setPrompt("Zeskanuj kod QR na samochodzie");
                integrator.initiateScan();
                return true;
            case R.id.changeWarehouse:
                warehouseScan = true;
                integrator.setPrompt("Zeskanuj kod QR magazynu");
                integrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                Toast.makeText(getApplicationContext(), "Skanowanie zostało anulowane", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                JsonObject jobj = new Gson().fromJson(result.getContents(), JsonObject.class);

                if(jobj.get("packId") != null && pack == null && packScan)
                    new AsyncGetPackage( "" + jobj.get("packId"), null, null).execute();
                else if(jobj.get("carId") != null && pack != null && carScan)
                    new AsyncGetPackage(null, "" + jobj.get("carId"), null).execute();
                else if(jobj.get("warehouseId") != null && pack != null && warehouseScan) {
                    new AsyncGetPackage(null, null, "" + jobj.get("warehouseId")).execute();
                }
                else if( packScan && jobj.get("packId") == null){
                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                    Toast.makeText(getApplicationContext(), "Zeskanuj prawidłowy kod QR!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Zeskanuj prawidłowy kod QR!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getPackage(String packId) {
        Call<Pack> call = packageService.getPack(packId);

        call.enqueue(new Callback<Pack>() {

            @Override
            public void onResponse(Call<Pack> call, Response<Pack> response) {
                if(response.isSuccessful()){        //gdy dane zostały prawidłowo pobrane z serwera ustawiamy odpowiednie pola w aktywności
                    pack = response.body();
                    Toast.makeText(getApplicationContext(), "OPERACJA UDANA", Toast.LENGTH_SHORT).show();
                    packageNumber.setText(pack.getPackageNumber());

                    if(pack.getDate() != null)
                        packageSendDate.setText(pack.getDate().split("T")[0]);

                    packageSize.setText(pack.getHeight() + "x" + pack.getLength() + "x" + pack.getWidth());
                    packageStatus.setText(pack.getPackageStatus().getName());

                    senderName.setText(pack.getSender().getName() + " " + pack.getSender().getLastName());
                    senderEmail.setText(pack.getSender().getEmail());
                    senderCompany.setText(pack.getSender().getCompanyName());
                    senderCity.setText(pack.getSender().getCity());
                    senderStreet.setText(pack.getSender().getPostCode() + ", " + pack.getSender().getStreet());
                    senderPhoneNumber.setText(pack.getSender().getPhoneNumber());

                    receiverName.setText(pack.getRecipient().getName() + " " + pack.getRecipient().getLastName());
                    receiverEmail.setText(pack.getRecipient().getEmail());
                    receiverCompany.setText(pack.getRecipient().getCompanyName());
                    receiverCity.setText(pack.getRecipient().getCity());
                    receiverStreet.setText(pack.getRecipient().getPostCode() + ", " + pack.getSender().getStreet());
                    receiverPhoneNumber.setText(pack.getRecipient().getPhoneNumber());


                    if(pack.getCar() != null){
                        carBrand.setText(pack.getCar().getBrand());
                        carModel.setText(pack.getCar().getModel());
                        carCapacity.setText(Double.toString(pack.getCar().getCapacity()));
                        carLicensePlate.setText(pack.getCar().getLicensePlate());
                        carStatus.setText(pack.getCar().getCarStatus().getName());
                    }

                    if(!pack.getWarehouses().isEmpty()) {
                        warehouseCity.setText(pack.getWarehouses().get(0).getCity());
                        warehousePhoneNumber.setText(pack.getWarehouses().get(0).getPhoneNumber());
                        warehousePostCode.setText(pack.getWarehouses().get(0).getPostCode());
                        warehouseStreet.setText(pack.getWarehouses().get(0).getStreet());
                        warehouseDescription.setText(pack.getWarehouses().get(0).getDescription());
                    }
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

    public void changeCar(String carId) {
        pack.getCar().setId(Long.valueOf(carId));
        Call<Pack> callPack = packageService.updatePack(pack);

        callPack.enqueue(new Callback<Pack>() {
            @Override
            public void onResponse(Call<Pack> call, Response<Pack> response) {
                pack = response.body();
            }

            @Override
            public void onFailure(Call<Pack> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void changeWarehouse(String warehouseId) {
        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(new Warehouse(Long.valueOf(warehouseId)));
        pack.setWarehouses(warehouses);

        Call<Pack> callPack = packageService.updatePack(pack);

        callPack.enqueue(new Callback<Pack>() {
            @Override
            public void onResponse(Call<Pack> call, Response<Pack> response) {
                if(response.isSuccessful()){
                    pack = response.body();
                }
                else if(response.code() == 401)
                    Toast.makeText(getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Pack> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void openDialog() {
        PackageStatusDialog packageStatusDialog = new PackageStatusDialog(pack);
        packageStatusDialog.show(getSupportFragmentManager(), "StatusDialog");
    }



    public class AsyncGetPackage extends AsyncTask<Void, Void, Void> {
        String packId;
        String carId;
        String warehouseId;

        public AsyncGetPackage(String packId, String carId, String warehouseId) {
            this.packId = packId;
            this.carId = carId;
            this.warehouseId = warehouseId;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(packId != null)
                getPackage(packId);
            if(carId != null)
                changeCar(carId);
            if(warehouseId != null)
                changeWarehouse(warehouseId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.packId = null;
            this.carId = null;
            this.warehouseId = null;

            warehouseScan = false;
            carScan = false;
            packScan = false;
            super.onPostExecute(aVoid);

        }
    }
}

