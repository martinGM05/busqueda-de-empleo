package com.example.proyectoappnativa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Entidades.User;
import com.google.android.material.textfield.TextInputEditText;

import com.example.proyectoappnativa.Firebase.fireService;

import java.util.List;

public class AuthActivity extends AppCompatActivity {

    TextInputEditText emailLogin;
    TextInputEditText passwordLogin;
    Button btnEntrar;
    TextView tView;

    fireService firebase = new fireService();
    String idUser;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_auth);
        Tools.setSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.white);
      //  getSupportActionBar().hide();

        emailLogin = findViewById(R.id.inputName);
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
        idUser = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
        if (!idUser.equals("0")) {

            // verficar si es ciudadano o empresa
            DbHelper dbHelper = new DbHelper(this);
            List<User> userData = dbHelper.getUserData(idUser);
            if (userData.size() > 0) {
                user = userData.get(0);
                if (user.getType().equals("Ciudadano")) {
                    //Toast.makeText(this, user.getType(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomePeoppleActivity.class));
                } else {
                    //Toast.makeText(this, user.getType(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                }
            }


            //startActivity(new Intent(this, HomeActivity.class));
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