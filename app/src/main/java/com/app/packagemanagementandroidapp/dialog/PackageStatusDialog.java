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

    private Pack pack;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    RadioButton inDelivery;
    RadioButton inWarehouse;
    RadioButton waitingForCourier;
    RadioButton wayToWarehouse;
    RadioButton packageDelivered;

    private PackageService packageService;

    public PackageStatusDialog(Pack pack) {
        this.pack = pack;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_package_status, null);

        view.findViewById(R.id.inWarehouse);
        radioGroup = view.findViewById(R.id.statusRadioGroup);
        Log.d("packId:", "" + pack.getPackageStatus().getId());

        inWarehouse = view.findViewById(R.id.inWarehouse);
        inDelivery = view.findViewById(R.id.inDelivery);
        waitingForCourier = view.findViewById(R.id.waitingForCourier);
        wayToWarehouse = view.findViewById(R.id.wayToWarehouse);
        packageDelivered = view.findViewById(R.id.packageDelivered);

        switch (pack.getPackageStatus().getId().intValue()){
            case 1:
                radioGroup.check(R.id.inDelivery);
                inWarehouse.setEnabled(false);
                waitingForCourier.setEnabled(false);
                wayToWarehouse.setEnabled(false);
                break;
            case 2:
                radioGroup.check(R.id.inWarehouse);
                waitingForCourier.setEnabled(false);
                packageDelivered.setEnabled(false);
                wayToWarehouse.setEnabled(false);
                break;
            case 3:
                radioGroup.check(R.id.waitingForCourier);
                inDelivery.setEnabled(false);
                inWarehouse.setEnabled(false);
                packageDelivered.setEnabled(false);
                break;
            case 4:
                radioGroup.check(R.id.wayToWarehouse);
                inDelivery.setEnabled(false);
                packageDelivered.setEnabled(false);
                waitingForCourier.setEnabled(false);
                break;
            case 5:
                radioGroup.check(R.id.packageDelivered);
                inWarehouse.setEnabled(false);
                inDelivery.setEnabled(false);
                waitingForCourier.setEnabled(false);
                wayToWarehouse.setEnabled(false);
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
                                        Toast.makeText(getDialog().getContext().getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();


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
