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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoappnativa.ToolsCarpet.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import com.example.proyectoappnativa.Firebase.fireService;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailRegister, nameRegister, passwordRegister, descriptionRegister, phoneRegister;
    private AutoCompleteTextView autoCompleteTextView;
    private Button btnRegistar;
    private TextView txtView;
    private final fireService firebase = new fireService();
    private ImageView ivFoto;
    private Uri imagenUri;
    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_IMAGE = 200;
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Resources res = getResources();
        autoCompleteTextView = findViewById(R.id.typeUser);
        ivFoto = findViewById(R.id.ivFoto);
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        Button btnSeleccionarImagen = findViewById(R.id.btnSelectImage);
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
        phoneRegister = findViewById(R.id.inputPhone);


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
            startActivityForResult(intent, TAKE_PHOTO);
        }
    }


    public void seleccionaImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE){
            assert data != null;
            imagenUri = data.getData();
            ivFoto.setImageURI(imagenUri);
        }else if(resultCode == RESULT_OK && requestCode == TAKE_PHOTO){
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get(getString(R.string.data));
            ivFoto.setImageBitmap(imgBitmap);
            imagenUri = getImageUri(this, imgBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, getString(R.string.title), null);
        return Uri.parse(path);
    }

    private void creatUser(){

        if(Tools.isConnection(this)){
            for(EditText editText : new EditText[]{emailRegister, nameRegister, passwordRegister, descriptionRegister, phoneRegister}){
                if(TextUtils.isEmpty(editText.getText().toString())){
                    editText.setError(getString(R.string.emptyField));
                    return;
                }
            }
            if(imagenUri == null){
                AlertDialog.showAlertDialog(this, getString(R.string.titleError), getString(R.string.insertImage));
                return;
            }

            String email = Objects.requireNonNull(emailRegister.getText()).toString();
            String password = Objects.requireNonNull(passwordRegister.getText()).toString();
            String name = Objects.requireNonNull(nameRegister.getText()).toString();
            String description = Objects.requireNonNull(descriptionRegister.getText()).toString();
            String phone = Objects.requireNonNull(phoneRegister.getText()).toString();
            String type = autoCompleteTextView.getText().toString();

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                AlertDialog.showAlertDialog(this, getString(R.string.validation), getString(R.string.invalidEmail));
                return;
            }else if(phone.length() != 10){
                AlertDialog.showAlertDialog(this, getString(R.string.validation), getString(R.string.invalidPhone));
                return;
            }else if(password.length() < 6){
                AlertDialog.showAlertDialog(this, getString(R.string.validation), getString(R.string.validationPassword));
                return;
            }
            firebase.uploadFirebaseUser(this, imagenUri, email, name, password, description, type, phone);
        }else{
            Snackbar.make(findViewById(R.id.btnEntrar), getString(R.string.No_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}