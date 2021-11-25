package com.example.proyectoappnativa.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.proyectoappnativa.AuthActivity;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.HomeActivity;
import com.example.proyectoappnativa.HomePeoppleActivity;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.RegisterActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Entidades.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fireService{


    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User userModel = new User();
    private SharedPreferences sharedPref;
    private StorageReference mStorageRef;
    String path, pathP;
    String name = "";
    String idDocument = "";
    String id;
    String userId;
    String type = "", typeUser = "";
    Intent intent;
    Task<DocumentSnapshot> dataUser;

    public void Auth(AuthActivity context, String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if(firebaseUser != null){
                        userId = firebaseUser.getUid();
                        sharedPref = context.getSharedPreferences("loginData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("loginCounter", true);
                        editor.putString("userId", userId);
                        editor.apply();

                        DbHelper dbHelper = new DbHelper(context);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        /*
                        if(db != null){
                            Toast.makeText(context, "Base de datos creada", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, "Error en la base de datos", Toast.LENGTH_LONG).show();
                        }
                        */
                        dataUser = getInfoUser(userId);
                        dataUser.addOnCompleteListener(documentSnapshot -> {
                            if(documentSnapshot.getResult().exists()){
                                typeUser = documentSnapshot.getResult().getString("type");
                                if(typeUser.equals("Ciudadano")){
                                    intent = new Intent(context, HomePeoppleActivity.class);
                                }else{
                                    intent = new Intent(context, HomeActivity.class);
                                }
                                context.startActivity(intent);
                            }
                        });
                    }
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

/*
    public void createDocument(RegisterActivity context){
        db.collection("users").document(userModel.getId()).set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Se ha creado el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
    Error
*/
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


    public void createDocumentPostulation(Activity context, Postulation postulation, Uri imageUri){
        db.collection("Postulaciones").add(postulation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                id = documentReference.getId();
                mStorageRef = FirebaseStorage.getInstance().getReference();
                path = "Fotos/" + id;
                StorageReference riversRef = mStorageRef.child(path);
                UploadTask uploadTask = riversRef.putFile(imageUri);
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
                            db.collection("Postulaciones").document(id).update("id", id, "image", path);
                            Toast.makeText(context, "Trabajo creado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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

    public Task<QuerySnapshot> getPostulationFirebase(String company){
        return db.collection("Postulaciones").whereEqualTo("company", company).get();
    }

    public Task<DocumentSnapshot> getApplicationsFirebase(String idDocument){
        return db.collection("Postulaciones").document(idDocument).get();
    }

    public void updateApplicationsFirebase(String idDocument, List<String> postulantes){
        db.collection("Postulaciones").document(idDocument).update("postulantes", postulantes);
    }

    public void deletePostulationFirebase(String idDocument){
        db.collection("Postulaciones").document(idDocument).delete();
    }

    public void updatePostulation(Activity activity, Postulation postulation, Uri imagenUri, short photo){
        if(photo == 1){
            id = postulation.getId();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            pathP = "Fotos/" + id;
            StorageReference imagenRef = mStorageRef.child(pathP);
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
                        db.collection("Postulaciones").document(postulation.getId()).update("name", postulation.getName(),
                                "description", postulation.getDescription(), "lat", postulation.getLat(), "lont", postulation.getLont(),"image", path);
                        Toast.makeText(activity, "Postulación actualizada", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            db.collection("Postulaciones").document(postulation.getId()).update("name", postulation.getName(), "description",
                    postulation.getDescription(), "lat", postulation.getLat(), "lont", postulation.getLont());
            Toast.makeText(activity, "Postulación actualizada", Toast.LENGTH_SHORT).show();
        }
    }

    public Task<QuerySnapshot> getPostulations(){
        return db.collection("Postulaciones").get();
    }

    public void addApplications(String idDocument, String idUser){
        db.collection("Postulaciones").document(idDocument).update("postulantes", FieldValue.arrayUnion(idUser));
    }

    public void saveToken(String token, String id){
        db.collection("Usuarios").document(id).update("token", token);
    }





}
