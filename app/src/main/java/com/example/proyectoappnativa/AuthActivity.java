package com.example.proyectoappnativa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    TextInputEditText emailLogin;
    TextInputEditText passwordLogin;
    Button btnEntrar;
    TextView tView;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_auth);
        Tools.setSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.white);
        getSupportActionBar().hide();

        emailLogin = findViewById(R.id.txtEmailRegister);
        passwordLogin = findViewById(R.id.txtPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tView = findViewById(R.id.txtRegresar);

        mAuth = FirebaseAuth.getInstance();

        btnEntrar.setOnClickListener(view -> {
            loginUser();
        });

        tView.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginUser(){

        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
            AlertDialog.alertEmptyFields(this);
        }else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText( AuthActivity.this, "User logged succesfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                        emailLogin.setText("");
                        passwordLogin.setText("");
                    }else{
                        Toast.makeText( AuthActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}