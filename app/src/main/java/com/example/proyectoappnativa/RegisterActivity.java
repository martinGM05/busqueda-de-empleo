package com.example.proyectoappnativa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Firebase.fireService;
import Models.User;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailRegister;
    TextInputEditText nameRegister;
    TextInputEditText passwordRegister;
    TextInputEditText descriptionRegister;
    AutoCompleteTextView autoCompleteTextView;

    Button btnRegistar;
    TextView txtView;
    fireService firebase = new fireService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Tools.setSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.white);
        getSupportActionBar().hide();
        Resources res = getResources();
        autoCompleteTextView = findViewById(R.id.typeUser);

        String [] typesUsers = res.getStringArray(R.array.typesUser);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.type_user, typesUsers);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        emailRegister = findViewById(R.id.txtEmailRegister);
        nameRegister = findViewById(R.id.txtNombreRegister);
        passwordRegister = findViewById(R.id.txtPassword);
        descriptionRegister = findViewById(R.id.txtDescripcion);

        btnRegistar = findViewById(R.id.btnEntrar);
        txtView = findViewById(R.id.txtRegresar);

        btnRegistar.setOnClickListener(view -> {
            creatUser();
        });

        txtView.setOnClickListener(view ->{
            startActivity(new Intent(this, AuthActivity.class));
        });
    }

    private void creatUser(){
        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String name = nameRegister.getText().toString();
        String description = descriptionRegister.getText().toString();
        String type = autoCompleteTextView.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(description)){
            AlertDialog.alertEmptyFields(this);
        }else{
           firebase.createUser(this, email, name, password, description, type);
           startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
        }
    }
}