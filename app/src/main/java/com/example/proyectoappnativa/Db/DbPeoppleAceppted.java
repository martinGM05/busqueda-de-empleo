package com.example.proyectoappnativa.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class DbPeoppleAceppted extends DbHelper{

    Context context;

    public DbPeoppleAceppted(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertPeopple(String id, String name, String postulation, String phone){
        long result = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("postulation", postulation);
            values.put("phone", phone);
            result = db.insert(TABLE_PEOPPLE_ACEPTED, null, values);
        }catch (Exception e){
            e.toString();
        }
        return result;
    }

    public void onUpgradePeoppleAcepted(SQLiteDatabase db) {
        try{
            db.execSQL("DROP TABLE " + TABLE_PEOPPLE_ACEPTED);
            createPeopple(db);
        }catch (Exception err){
            createPeopple(db);
        }
    }
}
