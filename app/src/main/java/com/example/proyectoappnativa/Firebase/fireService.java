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
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.HomeActivity;
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
import com.example.proyectoappnativa.Models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class fireService{


    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User userModel = new User();
    private SharedPreferences sharedPref;
    private StorageReference mStorageRef;
    private String path, pathP;
    private String name = "";
    private String idDocument = "";
    private String idPostulation;
    private String userId;
    private String type = "", typeUser = "";
    private Intent intent;
    private Task<DocumentSnapshot> dataUser;
    private Task<QuerySnapshot> data;
    private final Fcm fcm = new Fcm();
    boolean duplicate = false;
    // Register and Login
    public void Auth(Activity context, String email, String password){
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if(firebaseUser != null){
                            userId = firebaseUser.getUid();
                            sharedPref = context.getSharedPreferences(context.getString(R.string.loginData), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean(context.getString(R.string.loginCounter), true);
                            editor.putString(context.getString(R.string.userId), userId);
                            editor.apply();



                            dataUser = getInfo(context.getString(R.string.Users), userId);
                            String token = sharedPref.getString(context.getString(R.string.tokenFCM), String.valueOf(Context.MODE_PRIVATE));

                            saveToken(context, token, userId);

                            dataUser.addOnCompleteListener(documentSnapshot -> {
                                if(documentSnapshot.getResult().exists()){
                                    typeUser = documentSnapshot.getResult().getString(context.getString(R.string.type));
                                    intent = new Intent(context, HomeActivity.class);
                                    intent.putExtra(context.getString(R.string.typeUser), typeUser);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }else{
                        Snackbar.make(context.findViewById(R.id.autLayout), R.string.Account_not_exist, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void createUser(Activity context, String email, String name, String password, String description, String type, String phone,String image){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userModel.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    userModel.setName(name);
                    userModel.setEmail(email);
                    userModel.setDescription(description);
                    userModel.setType(type);
                    userModel.setPhone(phone);
                    userModel.setImageURL(image);
                    createDocument(context);
                }
            }
        });
    }

    // Create Documents

    public void createDocument(Activity context){
        db.collection(context.getString(R.string.UsuariosFirebase))
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
        db.collection(context.getString(R.string.PostulacionesFirebase)).add(postulation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                idPostulation = documentReference.getId();
                mStorageRef = FirebaseStorage.getInstance().getReference();
                path = context.getString(R.string.FotosURL) + idPostulation;
                StorageReference riversRef = mStorageRef.child(path);
                UploadTask uploadTask = riversRef.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            path = downloadUri.toString();
                            db.collection(context.getString(R.string.PostulacionesFirebase)).document(idPostulation).update(context.getString(R.string.id), idPostulation, context.getString(R.string.image), path);
                            Toast.makeText(context, R.string.jobCreated, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, R.string.titleError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void uploadFirebaseUser(Activity context, Uri imagenUri, String email, String name, String password, String description, String type, String phone){

        db.collection(context.getString(R.string.UsuariosFirebase)).whereEqualTo(context.getString(R.string.emailFirebase), email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()){
                        duplicate = true;
                    }
                    if(duplicate){
                        Snackbar.make(context.findViewById(R.id.registerLayout), R.string.email_exists, Snackbar.LENGTH_SHORT).show();
                    }else{
                        mStorageRef = FirebaseStorage.getInstance().getReference();
                        path = R.string.FotosURL + email;
                        StorageReference imagenRef = mStorageRef.child(path);
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
                                    createUser(context, email, name, password, description, type, phone, path);
                                    context.startActivity(new Intent(context, AuthActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void applyPostulate(Activity context, String idPostulate, String idUser){
        db.collection(context.getString(R.string.PostulacionesFirebase)).document(idPostulate).update(context.getString(R.string.postulantesFirebaseField), FieldValue.arrayRemove(idUser));
        db.collection(context.getString(R.string.PostulacionesFirebase)).document(idPostulate).update(context.getString(R.string.postulantesAceptedFirebase), FieldValue.arrayUnion(idUser));
        db.collection(context.getString(R.string.UsuariosFirebase)).document(idUser).update(context.getString(R.string.postulantesAceptedFirebase), FieldValue.arrayUnion(idPostulate));
    }

    // Get info

    public Task<DocumentSnapshot> getInfo(String document, String id){
        return db.collection(document).document(id).get();
    }

    public Task<QuerySnapshot> getPostulationFirebase(Activity context, String company){
        return db.collection(context.getString(R.string.PostulacionesFirebase)).whereEqualTo(context.getString(R.string.companyFirebaseField), company).get();
    }

    public Task<QuerySnapshot> getPostulations(Activity context){
        return db.collection(context.getString(R.string.PostulacionesFirebase)).get();
    }

    public Task<QuerySnapshot> getPeoppleAcepted(Activity context, String company) {
        return db.collection(context.getString(R.string.PostulacionesFirebase)).whereEqualTo(context.getString(R.string.companyFirebaseField), company).get();
    }



    // Update info

    public void updateUser(Activity context,String id, String name, String email,String description, String type, Uri imagenUri, String phone, Short photo){
        if(photo > 0){
            mStorageRef = FirebaseStorage.getInstance().getReference();
            path = R.string.FotosURL + id;
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
                        db.collection(context.getString(R.string.UsuariosFirebase))
                                .document(id).update(context.getString(R.string.nameFirebaseField), name, context.getString(R.string.descriptionFirebaseField), description, context.getString(R.string.imageFirebaseField), path).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            DbUsers dbUsers = new DbUsers(context);
                                            dbUsers.updateUser(id, name, email, description, type, phone,path);
                                        }
                                    }
                                }
                        );
                    }
                }
            });
        }else{
            db.collection(context.getString(R.string.UsuariosFirebase))
                    .document(id).update(context.getString(R.string.nameFirebaseField), name, context.getString(R.string.descriptionFirebaseField), description, "phone",phone ).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                DbUsers dbUsers = new DbUsers(context);
                                dbUsers.updateUser(id, name, email, description, type, phone, "");
                            }
                        }
                    }
            );
        }
    }

    public void updateApplicationsFirebase(Activity context, String idDocument, List<String> postulantes){
        db.collection(context.getString(R.string.PostulacionesFirebase)).document(idDocument).update(context.getString(R.string.postulantesFirebaseField), postulantes);
    }

    public void updatePostulation(Activity context, Postulation postulation, Uri imagenUri, short photo){
        if(photo == 1){
            idPostulation = postulation.getId();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            pathP = R.string.FotosURL + idPostulation;
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
                        db.collection(context.getString(R.string.PostulacionesFirebase)).document(postulation.getId()).update(
                                context.getString(R.string.nameFirebaseField), postulation.getName(),
                                context.getString(R.string.descriptionFirebaseField), postulation.getDescription(),
                                context.getString(R.string.latitude), postulation.getLat(),
                                context.getString(R.string.longitude), postulation.getLont(),
                                context.getString(R.string.imageField), path);
                        Toast.makeText(context, R.string.postulationUpdated, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            db.collection(context.getString(R.string.PostulacionesFirebase)).document(postulation.getId()).update(
                    context.getString(R.string.nameFirebaseField), postulation.getName(),
                    context.getString(R.string.descriptionFirebaseField),postulation.getDescription(),
                    context.getString(R.string.latitude), postulation.getLat(),
                    context.getString(R.string.longitude), postulation.getLont());
            Toast.makeText(context, R.string.postulationUpdated, Toast.LENGTH_SHORT).show();
        }
    }

    public void addApplications(Activity context, String idDocument, String idUser){
        dataUser = getInfo(context.getString(R.string.PostulacionesFirebase), idDocument);
        dataUser.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        List<String> postulantes = (List<String>) document.get(context.getString(R.string.postulantesAceptedFirebase));
                        if(postulantes != null){
                            if(postulantes.contains(idUser)){
                                Snackbar.make(context.findViewById(R.id.content), R.string.Applied_This_Offer, Snackbar.LENGTH_LONG).setBackgroundTint(context.getColor(R.color.primaryDarkColor)).show();
                            }else{
                                postulantes.add(idUser);
                                db.collection(context.getString(R.string.PostulacionesFirebase)).document(idDocument).update(context.getString(R.string.postulantesFirebaseField), FieldValue.arrayUnion(idUser));
                                Snackbar.make(context.findViewById(R.id.content), R.string.addApplications, Snackbar.LENGTH_LONG).setBackgroundTint(context.getColor(R.color.secondaryDarkColor)).show();
                            }
                        }else{
                            postulantes = new ArrayList<>();
                            postulantes.add(idUser);
                            db.collection(context.getString(R.string.PostulacionesFirebase)).document(idDocument).update(context.getString(R.string.postulantesFirebaseField), FieldValue.arrayUnion(idUser));
                            Snackbar.make(context.findViewById(R.id.content), R.string.addApplications, Snackbar.LENGTH_LONG).setBackgroundTint(context.getColor(R.color.secondaryDarkColor)).show();
                        }
                    }
                }
            }
        });
    }

    public void saveToken(Activity context, String token, String id){
        db.collection(context.getString(R.string.UsuariosFirebase)).document(id).update(context.getString(R.string.tokenFCM), token);
    }

    // Delete info

    public void deletePostulationFirebase(Activity context, String idDocument){
        db.collection(context.getString(R.string.PostulacionesFirebase)).document(idDocument).delete();
    }

    public void deleteToken(Activity context, String id){
        db.collection(context.getString(R.string.UsuariosFirebase)).document(id).update(context.getString(R.string.tokenFCM), FieldValue.delete());
    }  

}
