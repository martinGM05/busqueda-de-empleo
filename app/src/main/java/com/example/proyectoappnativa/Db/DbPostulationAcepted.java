package com.example.proyectoappnativa.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class DbPostulationAcepted extends DbHelper{

    Context context;

    public DbPostulationAcepted(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertPostulationAcepted(String id, String name, String company, String description, String keywords){
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
            result = db.insert(TABLE_POSTULATION_ACEPTED, null, values);
        }catch (Exception e){
            e.toString();
        }
        return result;
    }

    public void onUpgradePostulationAceptedDb(SQLiteDatabase db) {
        try{
            db.execSQL("DROP TABLE " + TABLE_POSTULATION_ACEPTED);
            createPostulationAcepted(db);
        }catch (Exception err){
            createPostulationAcepted(db);
        }
    }

}
