package com.example.proyectoappnativa.Db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class DbPostulation extends DbHelper{


    Context context;

    public DbPostulation(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertPostulation(String id, String name, String company, String description, String keywords){
        long result = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("company", company);
            values.put("description", description);
            values.put("keywords", keywords);
            result = db.insert(TABLE_POSTULATION, null, values);
        }catch (Exception e){
            e.toString();
        }
        return result;
    }

    public void onUpgradePostulationDb(SQLiteDatabase db) {
        try{
            db.execSQL("DROP TABLE " + TABLE_POSTULATION);
            createPostulation(db);
        }catch (Exception err){
            createPostulation(db);
        }
    }
}
