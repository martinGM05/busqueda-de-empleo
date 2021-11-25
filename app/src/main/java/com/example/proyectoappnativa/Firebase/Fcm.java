package com.example.proyectoappnativa.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.proyectoappnativa.HomePeoppleActivity;
import com.example.proyectoappnativa.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Fcm extends FirebaseMessagingService {



    private SharedPreferences sharedPref;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("token", "mi token es: "+s);

        sharedPref = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("loginCounter", true);
        editor.putString("token", s);
        editor.apply();


        //saveToken(s);
    }

    /*
        public void saveToken(String s){
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            String idUser = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
            firebase.saveToken(s, idUser);
        }
    */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();

        /*
        Log.e("TAG", "Mensaje recibido de "+from);

        if(remoteMessage.getNotification() != null){
            Log.e("TAG", "Titulo: "+remoteMessage.getNotification().getTitle());
            Log.e("TAG", "Body: "+remoteMessage.getNotification().getBody());
        }
        */

        if(remoteMessage.getData().size() > 0){

            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");

            mayorqueoreo(titulo, detalle);



        }

    }

    private void mayorqueoreo(String titulo, String detalle) {
        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
        NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
        nc.setShowBadge(true);
        nm.createNotificationChannel(nc);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(detalle)
                .setContentIntent(clickNoti())
                .setContentInfo("Nuevo");

        Random random = new Random();
        int idNoify = random.nextInt(8000);
        nm.notify(idNoify, builder.build());
    }

    public PendingIntent clickNoti(){
        Intent nf = new Intent(getApplicationContext(), HomePeoppleActivity.class);
        nf.putExtra("color", "rojo");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, nf, 0);
    }

}
