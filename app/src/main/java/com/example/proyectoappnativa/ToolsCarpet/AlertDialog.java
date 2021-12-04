package com.example.proyectoappnativa.ToolsCarpet;


import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

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

    public static void showAlertDialog(Activity registerActivity, String title, String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(registerActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

}
