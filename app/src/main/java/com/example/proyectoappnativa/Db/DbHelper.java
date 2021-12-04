package com.example.proyectoappnativa.Db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.PeoppleAcepted;
import com.example.proyectoappnativa.Models.User;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NOMBRE = "busquedaEmpleo.db";
    public static final String TABLE_USUARIO = "Usuario";
    public static final String TABLE_PEOPPLE_ACEPTED = "PeopleAcepted";
    public static final String TABLE_POSTULATION = "Postulation";
    public static final String TABLE_POSTULATION_ACEPTED = "PostulationAcepted";
    SQLiteDatabase db = this.getReadableDatabase();

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
                "phone TEXT NOT NULL," +
                "imageURL TEXT NOT NULL)");
    }

    public void createPostulation(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_POSTULATION + "(" + "" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL," +
                "company TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "keywords TEXT NOT NULL," +
                "image BLOB)");
    }

    public void createPostulationAcepted(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_POSTULATION_ACEPTED + "(" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL, " +
                "company TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "keywords TEXT NOT NULL, " +
                "image BITMAP)");
    }

    public void createPeopple(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_PEOPPLE_ACEPTED + "(" + "" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL," +
                "postulation TEXT NOT NULL," +
                "phone TEXT NOT NULL," +
                "image BLOB)");
    }

    public List<User> getUserData(String id){
        List<User> userList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE id = '" + id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setDescription(cursor.getString(3));
                user.setType(cursor.getString(4));
                user.setPhone(cursor.getString(5));
                user.setImageURL(cursor.getString(6));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public ArrayList<PostulationOff> getPostulationsData(){
        ArrayList<PostulationOff> postulationsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTULATION, null);
        if (cursor.moveToFirst()) {
            do {
                PostulationOff postulationOff = new PostulationOff();
                postulationOff.setId(cursor.getString(0));
                postulationOff.setName(cursor.getString(1));
                postulationOff.setCompany(cursor.getString(2));
                postulationOff.setDescription(cursor.getString(3));
                postulationOff.setKerywords(cursor.getString(4));
                postulationsList.add(postulationOff);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return postulationsList;
    }

    public ArrayList<PostulationOff> getPostulationsAceptedData(){
        ArrayList<PostulationOff> postulationsAceptedList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTULATION_ACEPTED, null);
        if (cursor.moveToFirst()) {
            do {
                PostulationOff postulationOff = new PostulationOff();
                postulationOff.setId(cursor.getString(0));
                postulationOff.setName(cursor.getString(1));
                postulationOff.setCompany(cursor.getString(2));
                postulationOff.setDescription(cursor.getString(3));
                postulationOff.setKerywords(cursor.getString(4));
                postulationsAceptedList.add(postulationOff);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return postulationsAceptedList;
    }

    public ArrayList<PeoppleAcepted> getPeoppleAcepted(){
        ArrayList<PeoppleAcepted> peoppleAceptedsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PEOPPLE_ACEPTED, null);
        if (cursor.moveToFirst()){
            do{
                PeoppleAcepted peoppleAcepted = new PeoppleAcepted();
                peoppleAcepted.setId(cursor.getString(0));
                peoppleAcepted.setName(cursor.getString(1));
                peoppleAcepted.setPostulation(cursor.getString(2));
                peoppleAcepted.setPhone(cursor.getString(3));
                peoppleAceptedsList.add(peoppleAcepted);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return peoppleAceptedsList;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE " + TABLE_USUARIO);
            onCreate(db);
        }catch (Exception err){
            onCreate(db);
        }
    }
}
