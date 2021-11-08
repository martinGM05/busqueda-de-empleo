package com.example.proyectoappnativa;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AlertDialog{

    public static void alertEmptyFields(Activity registerActivity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(registerActivity);
        builder.setTitle(R.string.titleError);
        builder.setMessage(R.string.descriptionEmpty);
        builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

}
