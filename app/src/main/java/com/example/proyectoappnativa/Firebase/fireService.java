package com.example.proyectoappnativa.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.proyectoappnativa.AuthActivity;
import com.example.proyectoappnativa.HomeActivity;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.RegisterActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Models.User;

public class fireService{


    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User userModel = new User();
    private SharedPreferences sharedPref;
    private StorageReference mStorageRef;
    String path;


    public void Auth(AuthActivity context, String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if(firebaseUser != null){
                        String userId = firebaseUser.getUid();
                        sharedPref = context.getSharedPreferences("loginData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("loginCounter", true);
                        editor.putString("userId", userId);
                        editor.apply();
                    }
                    DbHelper dbHelper = new DbHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if(db != null){
                        Toast.makeText(context, "Base de datos creada", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "Error en la base de datos", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText( context, R.string.userLogged, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                }else{
                    Toast.makeText( context, R.string.userLoggedError + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUser(RegisterActivity context, String email, String name, String password, String description, String type, String image){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userModel.setId(mAuth.getCurrentUser().getUid());
                    userModel.setName(name);
                    userModel.setEmail(email);
                    userModel.setDescription(description);
                    userModel.setType(type);
                    userModel.setImageURL(image);
                    createDocument(context);
                }
            }
        });
    }

    public Task<DocumentSnapshot> getInfoUser(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("Usuarios").document(id).get();
    }

    public void createDocument(Activity context){
        db.collection("Usuarios")
                .document(userModel.getId()).set(userModel).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, R.string.userCreated, Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(context, R.string.userCreatedError, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    public void updateUser(Activity context, String id, String name, String email,String description, String type, Uri imagenUri){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        path = "Fotos/" + id;
        StorageReference riversRef = mStorageRef.child(path);
        UploadTask uploadTask = riversRef.putFile(imagenUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    path = downloadUri.toString();
                    db.collection("Usuarios")
                            .document(id).update("name", name, "description", description, "imageURL", path).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        DbUsers dbUsers = new DbUsers(context);
                                        dbUsers.updateUser(id, name, email, description, type, path);
                                    }else{
                                        Toast.makeText(context, "Nell", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                } else {
                    Toast.makeText(context, "Nell", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void uploadFirebaseUser(Uri imagenUri, RegisterActivity context, String email, String name, String password, String description, String type){
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Fotos");
        final StorageReference imagenRef = mStorageRef.child(imagenUri.getLastPathSegment());
        UploadTask uploadTask = imagenRef.putFile(imagenUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagenRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    path = downloadUri.toString();
                    createUser(context, email, name, password, description, type, path);
                }
            }
        });
    }


}
