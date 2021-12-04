package com.example.proyectoappnativa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbPostulationAcepted;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.ToolsCarpet.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.example.proyectoappnativa.Firebase.fireService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private TextInputEditText emailLogin;
    private TextInputEditText passwordLogin;
    private Button btnEntrar;
    private TextView tView;
    private fireService firebase = new fireService();
    private String idUser;
    private User user = new User();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_auth);
        Tools.setSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.white);
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
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.loginData), MODE_PRIVATE);
        idUser = sharedPreferences.getString(getString(R.string.userId), String.valueOf(MODE_PRIVATE));

        DbHelper dbHelper = new DbHelper(this);
        DbUsers dbUsers = new DbUsers(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!idUser.equals("0")) {
                List<User> userData = dbHelper.getUserData(idUser);
                if (userData.size() > 0) {
                    user = userData.get(0);
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra(getString(R.string.typeUser), user.getType());
                    startActivity(intent);
                }

        }
    }


    private void loginUser(){
        if(Tools.isConnection(this)){
            for(EditText editText : new EditText[]{emailLogin, passwordLogin}){
                if(TextUtils.isEmpty(editText.getText().toString())){
                    editText.setError(getString(R.string.emptyField));
                    return;
                }
            }
            String email = Objects.requireNonNull(emailLogin.getText()).toString();
            String password = Objects.requireNonNull(passwordLogin.getText()).toString();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                AlertDialog.showAlertDialog(this, getString(R.string.validation), getString(R.string.invalidEmail));
                return;
            }else if(password.length() < 6){
                AlertDialog.showAlertDialog(this, getString(R.string.validation), getString(R.string.validationPassword));
                return;
            }
            firebase.Auth(this, email, password);
            emailLogin.setText("");
            passwordLogin.setText("");
        }else{
            Snackbar.make(findViewById(R.id.btnEntrar), getString(R.string.dont_conection), Snackbar.LENGTH_LONG).show();
        }
    }
}