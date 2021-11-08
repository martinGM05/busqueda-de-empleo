package com.example.proyectoappnativa;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Tools.setSystemBarLight(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.btnFuga);

        btnLogout.setOnClickListener(view -> {
            signOut();
        });

    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}