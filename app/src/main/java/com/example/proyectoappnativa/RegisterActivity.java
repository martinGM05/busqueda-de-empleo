package com.example.proyectoappnativa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoappnativa.ToolsCarpet.AlertDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;

import com.example.proyectoappnativa.Firebase.fireService;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailRegister;
    TextInputEditText nameRegister;
    TextInputEditText passwordRegister;
    TextInputEditText descriptionRegister;
    AutoCompleteTextView autoCompleteTextView;

    Button btnRegistar;
    TextView txtView;
    fireService firebase = new fireService();
    ImageView ivFoto;
    Button btnTomarFoto, btnSeleccionarImagen;

    Uri imagenUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Resources res = getResources();
        autoCompleteTextView = findViewById(R.id.typeUser);

        ivFoto = findViewById(R.id.ivFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = findViewById(R.id.btnSelectImage);

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },0);
        }

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionaImagen();
            }
        });

        String [] typesUsers = res.getStringArray(R.array.typesUser);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.type_user, typesUsers);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        emailRegister = findViewById(R.id.inputName);
        nameRegister = findViewById(R.id.txtNombreRegister);
        passwordRegister = findViewById(R.id.txtPassword);
        descriptionRegister = findViewById(R.id.inputDescription);

        btnRegistar = findViewById(R.id.btnEntrar);
        txtView = findViewById(R.id.txtRegresar);

        btnRegistar.setOnClickListener(view -> {
            creatUser();
        });

        txtView.setOnClickListener(view ->{
            startActivity(new Intent(this, AuthActivity.class));
        });
    }

    public void tomarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }


    public void seleccionaImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELEC_IMAGEN){
            imagenUri = data.getData();
            ivFoto.setImageURI(imagenUri);
        }else if(resultCode == RESULT_OK && requestCode == 1){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imgBitmap);
            imagenUri = getImageUri(this, imgBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
           firebase.uploadFirebaseUser(imagenUri, this, email, name, password, description, type);
           startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
        }
    }
}