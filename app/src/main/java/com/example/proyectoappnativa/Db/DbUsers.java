package com.example.proyectoappnativa.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.proyectoappnativa.Models.User;

public class DbUsers extends DbHelper{

    Context context;

    public DbUsers(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarUsuario(String id, String name, String email, String description, String type, String phone ,String imageURL){
        long uid = 0;
        try{
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("email", email);
            values.put("description", description);
            values.put("type", type);
            values.put("phone", phone);
            values.put("imageURL", imageURL);
            uid = db.insert(TABLE_USUARIO, null, values);
        }catch (Exception ex){
            ex.toString();
        }
        return uid;
    }

    public long updateUser(String id, String name, String email, String description, String type, String phone,String imageURL){
        long uid = 0;
        try{
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("email", email);
            values.put("description", description);
            values.put("type", type);
            values.put("phone", phone);
            values.put("imageURL", imageURL);
            uid = db.update(TABLE_USUARIO, values, "id = ?", new String[]{id});
        }catch (Exception ex){
            ex.toString();
        }
        return uid;
    }
}
