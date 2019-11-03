package com.app.packagemanagementandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.packagemanagementandroidapp.model.Login;
import com.app.packagemanagementandroidapp.model.authResponse;
import com.app.packagemanagementandroidapp.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://192.168.0.2:8080/")
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit = builder.build();

    UserService userService = retrofit.create(UserService.class);

    EditText loginInput;
    EditText passwordInput;
    TextView errorLabel;
    ProgressBar progressBar;

    private static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        loginInput = findViewById(R.id.login);
        passwordInput = findViewById(R.id.password);
        errorLabel = findViewById(R.id.errorLogin);
        getSupportActionBar().hide();

        findViewById(R.id.loginButton).setOnClickListener((view) ->  { new StoreDataToDB().execute(); } );
    }

    private void login(){
        Login login = new Login(loginInput.getText().toString(), passwordInput.getText().toString());
        Log.d("MA", "Login: " + login.getLogin().toString());

        Call<authResponse> call = userService.authorization(login);

        SharedPreferences sharedPref = getSharedPreferences("pref", 0);

        call.enqueue(new Callback<authResponse>() {
            @Override
            public void onResponse(Call<authResponse> call, Response<authResponse> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.putExtra("TOKEN", response.body().getJwttoken());

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TOKEN", "Bearer " + response.body().getJwttoken());
                    editor.commit();

                    startActivity(intent);
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
