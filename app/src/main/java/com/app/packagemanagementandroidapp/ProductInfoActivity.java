package com.app.packagemanagementandroidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.packagemanagementandroidapp.model.Product;
import com.app.packagemanagementandroidapp.service.ProductService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductInfoActivity extends AppCompatActivity {

    TextView productName;
    TextView productCategory;
    TextView productWeight;
    TextView productPickingTime;
    TextView productPackageNumber;

    SwipeRefreshLayout refreshLayout;

    Product product;
    ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Informacje o produkcie");

        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productWeight = findViewById(R.id.productWeight);
        productPickingTime = findViewById(R.id.productPickingTime);
        productPackageNumber = findViewById(R.id.productPackageNumber);

        refreshLayout = findViewById(R.id.swipeToRefreshProduct);

        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        productService = ServiceGenerator.createService(ProductService.class, sharedPref.getString("TOKEN", ""));

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setPrompt("Zeskanuj kod QR produktu");
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

                if(jobj.get("productId") != null) {
                    new AsyncGetProduct(jobj.get("productId").toString(), jobj.get("packNumber").toString()).execute();
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

    public void getProduct(String productId, String packNumber) {
        Call<Product> call = productService.getProduct(productId);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()){
                    product = response.body();
                    productName.setText(product.getName());
                    productCategory.setText(product.getCategory().getName());
                    productWeight.setText(String.valueOf(product.getWeight()));
                    //productPickingTime.setText(product.getContent().getDate());
                    productPackageNumber.setText(packNumber);
                }
                else if(response.code() == 401)
                    Toast.makeText(getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
            }
        });

    }


    public class AsyncGetProduct extends AsyncTask<Void, Void, Void> {
        String productId;
        String packNumber;

        public AsyncGetProduct(String productId, String packNumber) {
            this.productId = productId;
            this.packNumber = packNumber;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(productId != null)
                getProduct(productId, packNumber);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.productId = null;
            this.packNumber = null;
            super.onPostExecute(aVoid);
        }
    }
}
