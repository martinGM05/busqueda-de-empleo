package com.example.proyectoappnativa;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Db.DbUsers;
import Db.DbHelper;
import Firebase.fireService;
import Models.User;

public class PruebasActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnLogout;
    Button btnCreateDB,  btnGuardarUser, btnBuscar;
    TextView textView;
    TextView textName, textNameOnline;
    TextView textEmail, textEmailOnline;
    TextView textDescription, textDescriptionOnline;
    fireService firebase = new fireService();
    User usuario = new User();

    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Tools.setSystemBarLight(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pruebas);
        btnLogout = findViewById(R.id.btnFuga);
        textView = findViewById(R.id.textId);
        btnCreateDB = findViewById(R.id.btnCrearBD);

        textName = findViewById(R.id.txtNombre);
        textEmail = findViewById(R.id.txtEmail);
        textDescription = findViewById(R.id.txtDescription);

        textNameOnline = findViewById(R.id.txtNameOnline);
        textEmailOnline = findViewById(R.id.txtEmailOnline);
        textDescriptionOnline = findViewById(R.id.txtDescriptionOnline);

        btnGuardarUser = findViewById(R.id.btnGuardar);
        btnBuscar = findViewById(R.id.btnBuscar);

        btnBuscar.setOnClickListener(view -> {
        });

        btnGuardarUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DbUsers dbUsers = new DbUsers(PruebasActivity.this);
                long uid = dbUsers.insertarUsuario(usuario.getId(), usuario.getName(), usuario.getEmail(), usuario.getDescription(), usuario.getType());
                if(uid > 0){
                    Toast.makeText(PruebasActivity.this, "Usuario guardado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PruebasActivity.this, "Usuario no guardado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(view -> {
            signOut();
        });

        btnCreateDB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DbHelper dbHelper = new DbHelper(PruebasActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if(db != null){
                    Toast.makeText(PruebasActivity.this, "Base de datos creada", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PruebasActivity.this, "Error en la base de datos", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    public void getInfoUserOffline(String id){
        DbHelper dbHelper = new DbHelper(PruebasActivity.this);
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                textName.setText(user.getName());
                textEmail.setText(user.getEmail());
                textDescription.setText(user.getDescription());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public void checkUserStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
        idUser = id;
        if(!id.isEmpty()){
            textView.setText(id);
            /*getInfoUser(id);
            getInfoUserOffline(id);*/
            if(isConnection()){
                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
               getInfoUser(id);
            }else{
                Toast.makeText(this, "No conectado", Toast.LENGTH_SHORT).show();
                getInfoUserOffline(id);
            }
        }else{
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    public void getInfoUser(String id){
        Task<DocumentSnapshot> data;
        data = firebase.getInfoUser(id);
        data.addOnCompleteListener(documentSnapshot -> {
            if(documentSnapshot.getResult().exists()){
                usuario.setId(documentSnapshot.getResult().getString("id"));
                textNameOnline.setText(documentSnapshot.getResult().getString("name"));
                textEmailOnline.setText(documentSnapshot.getResult().getString("email"));
                textDescriptionOnline.setText(documentSnapshot.getResult().getString("description"));
                usuario.setType(documentSnapshot.getResult().getString("type"));
            }
        });
    }

    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }
}