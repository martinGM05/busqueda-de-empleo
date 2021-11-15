package com.example.proyectoappnativa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import Db.DbHelper;
import Firebase.fireService;

public class AuthActivity extends AppCompatActivity {

    TextInputEditText emailLogin;
    TextInputEditText passwordLogin;
    Button btnEntrar;
    TextView tView;

    fireService firebase = new fireService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_auth);
        Tools.setSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.white);
      //  getSupportActionBar().hide();

        emailLogin = findViewById(R.id.txtEmailRegister);
        passwordLogin = findViewById(R.id.txtPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tView = findViewById(R.id.txtRegresar);

        btnEntrar.setOnClickListener(view -> {
           loginUser();
        });

        tView.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        String email = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
        if (!email.equals("0")) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }


    private void loginUser(){
        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
            AlertDialog.alertEmptyFields(this);
        }else{
            emailLogin.setText("");
            passwordLogin.setText("");
            firebase.Auth(this, email, password);
        }
    }

}