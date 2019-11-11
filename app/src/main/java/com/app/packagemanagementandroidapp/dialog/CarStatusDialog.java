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
import com.app.packagemanagementandroidapp.model.Car;
import com.app.packagemanagementandroidapp.service.CarService;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarStatusDialog extends AppCompatDialogFragment {

    Car car;
    CarService carService;

    RadioGroup radioGroup;
    RadioButton radioButton;


    public CarStatusDialog(Car car) {
        this.car = car;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_car_status, null);


        radioGroup = view.findViewById(R.id.statusCarRadioGroup);
        Log.d("carId:", "" + car.getCarStatus().getId());

        switch (car.getCarStatus().getId().intValue()){
            case 1:
                radioGroup.check(R.id.inTheField);
                break;
            case 2:
                radioGroup.check(R.id.isFree);
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
                            case "W terenie":
                                car.getCarStatus().setId(1L);
                                break;
                            case "Wolny":
                                car.getCarStatus().setId(2L);
                                break;
                        }

                        SharedPreferences sharedPref = getContext().getSharedPreferences("pref", 0);
                        carService = ServiceGenerator.createService(CarService.class, sharedPref.getString("TOKEN", ""));

                        Call<Car> call = carService.updateCar(car);

                        call.enqueue(new Callback<Car>() {
                            @Override
                            public void onResponse(Call<Car> call, Response<Car> response) {
                                if(response.isSuccessful()){

                                }
                                else if(response.code() == 401)
                                    Toast.makeText(getContext().getApplicationContext(), "Bład autoryzacji", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Car> call, Throwable t) {
                                Toast.makeText(getContext(), "Błąd połączenia! Sprawdź połączenie internetowe", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });

        return builder.create();
    }
}
