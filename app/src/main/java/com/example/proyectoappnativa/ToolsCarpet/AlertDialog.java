package com.example.proyectoappnativa.ToolsCarpet;


import android.app.Activity;

import com.example.proyectoappnativa.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class AlertDialog{

    FirebaseAuth mAuth;

    public static void alertEmptyFields(Activity registerActivity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(registerActivity);
        builder.setTitle(R.string.titleError);
        builder.setMessage(R.string.descriptionEmpty);
        builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public static void alerta2(Activity context, String Message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.titleError);
        builder.setMessage(Message);
        builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public static void alertSignOut(Activity context, String Message){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Sesión");
        builder.setMessage(Message);
        builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


}
