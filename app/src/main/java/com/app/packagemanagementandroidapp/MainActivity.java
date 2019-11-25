package com.app.packagemanagementandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.packagemanagementandroidapp.model.Login;
import com.app.packagemanagementandroidapp.model.Role;
import com.app.packagemanagementandroidapp.model.authResponse;
import com.app.packagemanagementandroidapp.service.ServiceGenerator;
import com.app.packagemanagementandroidapp.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    UserService userService;

    EditText loginInput;
    EditText passwordInput;
    TextView errorLabel;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userService = ServiceGenerator.createService(UserService.class);

        progressBar = findViewById(R.id.progressBar);
        loginInput = findViewById(R.id.login);
        passwordInput = findViewById(R.id.password);
        errorLabel = findViewById(R.id.errorLogin);
        getSupportActionBar().hide();

        findViewById(R.id.loginButton).setOnClickListener((view) ->  { new StoreDataToDB().execute(); } );
    }

    private void login(){
        Call<authResponse> call = userService.authorization(new Login(loginInput.getText().toString(), passwordInput.getText().toString()));
        SharedPreferences sharedPref = getSharedPreferences("pref", 0);

        call.enqueue(new Callback<authResponse>() {
            @Override
            public void onResponse(Call<authResponse> call, Response<authResponse> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    //jeśli użytkownik ma przypisaną rolę WORKER

                    boolean isWorker = false;
                    for(Role role : response.body().getRoles()) {
                        if(role.getName().contains("ROLE_ADMIN") || role.getName().contains("ROLE_WORKER"))
                            isWorker = true;
                    }

                    if(!isWorker) {
                        errorLabel.setVisibility(TextView.VISIBLE);
                    }
                    else {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("TOKEN", "Bearer " + response.body().getJwttoken());
                        editor.commit();
                        startActivity(intent);
                    }
                }
                else{
                    errorLabel.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<authResponse> call, Throwable t) {
                errorLabel.setVisibility(TextView.VISIBLE);
                Log.d("ERROR", "ERROR: " + t.getMessage() );
            }
        });
    }


    public class StoreDataToDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
            errorLabel.setVisibility(TextView.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            login();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

}
