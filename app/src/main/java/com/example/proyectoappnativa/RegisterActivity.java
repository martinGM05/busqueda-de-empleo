package com.example.proyectoappnativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    ImageView ivFoto;
    Button btnTomarFoto, btnSeleccionarImagen;

    Uri imagenUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;

    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path;

    private StorageReference mStorageRef;
    ProgressBar cargando;

    Bitmap thumb_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Resources res = getResources();
        autoCompleteTextView = findViewById(R.id.typeUser);

        ivFoto = findViewById(R.id.ivFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Fotos");
        cargando = new ProgressBar(this);

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
            // subir imagen a firebase
            subirImagenAFirebase(imagenUri);

        }else if(resultCode == RESULT_OK && requestCode == 1){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imgBitmap);
            // subir imagen a firebase
            subirImagenAFirebase(getImageUri(this, imgBitmap));
        }
    }

    // getImageUri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void subirImagenAFirebase(Uri imagenUri){
        final StorageReference imagenRef = mStorageRef.child(imagenUri.getLastPathSegment());
        UploadTask uploadTask = imagenRef.putFile(imagenUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imagenRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    path = downloadUri.toString();
                    Log.d("path", path);
                } else {
                    Log.e("Error", path);
                }
            }
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