package com.example.proyectoappnativa.Db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.example.proyectoappnativa.Models.User;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NOMBRE = "busquedaEmpleo.db";
    public static final String TABLE_USUARIO = "Usuario";


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USUARIO + "(" + "" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "imageURL TEXT NOT NULL)");
    }

    public List<User> getUserData(String id){
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE id = '" + id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setDescription(cursor.getString(3));
                user.setType(cursor.getString(4));
                user.setImageURL(cursor.getString(5));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_USUARIO);
        onCreate(db);
    }
}
