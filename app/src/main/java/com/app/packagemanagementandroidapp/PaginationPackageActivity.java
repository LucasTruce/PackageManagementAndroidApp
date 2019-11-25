package com.app.packagemanagementandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.packagemanagementandroidapp.model.Pack;
import com.app.packagemanagementandroidapp.model.PackageResponse;
import com.app.packagemanagementandroidapp.service.PackageService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.app.packagemanagementandroidapp.utils.PaginationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginationPackageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;

    private PackageService packageService;
    private PaginationAdapter adapter;

    private int pageNumber = 0;

    //variables for pagination
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagination_package);

        progressBar = findViewById(R.id.recycler_progress);
        recyclerView = findViewById(R.id.main_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPref = getSharedPreferences("pref", 0);
        packageService = ServiceGenerator.createService(PackageService.class, sharedPref.getString("TOKEN", ""));

        Call<PackageResponse> call = packageService.getList(pageNumber);

        call.enqueue(new Callback<PackageResponse>() {
            @Override
            public void onResponse(Call<PackageResponse> call, Response<PackageResponse> response) {

                List<Pack> packages = response.body().getContent();
                Log.d("AAAAA:", response.body().getContent() + "");
                adapter = new PaginationAdapter(packages, PaginationPackageActivity.this);
                recyclerView.setAdapter(adapter);

                //Toast.makeText(PaginationPackageActivity.this, response.body().getPageNumber(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PackageResponse> call, Throwable t) {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if(dy>0){
                    if(isLoading){
                        if(totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if(!isLoading && (pastVisibleItems + visibleItemCount) >= totalItemCount){
                        pageNumber++;
                        performPagination();
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void performPagination() {

        progressBar.setVisibility(View.VISIBLE);
        Call<PackageResponse> call = packageService.getList(pageNumber);

        call.enqueue(new Callback<PackageResponse>() {
            @Override
            public void onResponse(Call<PackageResponse> call, Response<PackageResponse> response) {

                if(response.body().getNumber() != response.body().getTotalPages()  ){
                    List<Pack> packages = response.body().getContent();
                    adapter.addPackages(packages);
                    Toast.makeText(PaginationPackageActivity.this, "Strona " + pageNumber, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PaginationPackageActivity.this, "Brak więcej danych do wyświetlenia...", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PackageResponse> call, Throwable t) {

            }
        });
    }
}
