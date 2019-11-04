package com.app.packagemanagementandroidapp.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.packagemanagementandroidapp.R;
import com.app.packagemanagementandroidapp.model.Pack;
import com.app.packagemanagementandroidapp.service.PackageService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageStatusDialog extends AppCompatDialogFragment {

    Pack pack;

    RadioGroup radioGroup;
    RadioButton radioButton;

    PackageService packageService;

    public PackageStatusDialog(Pack pack) {
        this.pack = pack;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);


        radioGroup = view.findViewById(R.id.statusRadioGroup);
        Log.d("packId:", "" + pack.getPackageStatus().getId());

        switch (pack.getPackageStatus().getId().intValue()){
            case 1:
                radioGroup.check(R.id.inDelivery);
                break;
            case 2:
                radioGroup.check(R.id.inWarehouse);
                break;
            case 3:
                radioGroup.check(R.id.waitingForCourier);
                break;
            case 4:
                radioGroup.check(R.id.wayToWarehouse);
                break;
            case 5:
                radioGroup.check(R.id.packageDelivered);
                break;
        }

        builder.setView(view)
                .setTitle("Zmień status paczki")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Zmień", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int id = radioGroup.getCheckedRadioButtonId();
                        radioButton = view.findViewById(id);
                        switch (radioButton.getText().toString()){
                            case "W dostawie":
                                pack.getPackageStatus().setId(1L);
                                break;
                            case "W magazynie":
                                pack.getPackageStatus().setId(2L);
                                break;
                            case "W oczekiwaniu na kuriera":
                                pack.getPackageStatus().setId(3L);
                                break;
                            case "W drodze do magazynu":
                                pack.getPackageStatus().setId(4L);
                                break;
                            case "Dostarczono":
                                pack.getPackageStatus().setId(5L);
                                break;
                        }


                        SharedPreferences sharedPref = getContext().getSharedPreferences("pref", 0);
                        packageService = ServiceGenerator.createService(PackageService.class, sharedPref.getString("TOKEN", ""));
                        Log.d("PACKAGE", "" + pack.getPackageStatus().getId() + pack.getPackageStatus().getName());
                        Call<Pack> call = packageService.updatePack(pack);

                        call.enqueue(new Callback<Pack>() {
                            @Override
                            public void onResponse(Call<Pack> call, Response<Pack> response) {
                                if(response.isSuccessful()){

                                }
                                else if(response.code() == 401)
                                        Toast.makeText(getContext().getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onFailure(Call<Pack> call, Throwable t) {
                                Toast.makeText(getContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        return builder.create();
    }
}
