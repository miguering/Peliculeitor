package com.example.practicaandroid.ui.Favoritos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.practicaandroid.ui.Peliculas.Result;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "peliculeitor";
    private static final String TABLE_PELIS= "peliculas";
    private static final String TABLE_SERIES= "series";
    private static final String KEY_ID = "id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        //creo 2 tablas!
        query = "CREATE TABLE IF NOT EXISTS " + TABLE_PELIS + "("
                + KEY_ID + " INTEGER PRIMARY KEY)";
        db.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS " + TABLE_SERIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY)";
        db.execSQL(query);

        Log.d("OnCreate SQLite", "BD comprobada.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PELIS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIES);

        onCreate(db);
    }

    // agrego a favoritos la nueva peli
    public void agregarPelicula(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // ID de la peli o serie

        // Inserting Row
        db.insert(TABLE_PELIS, null, values);

        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }



    public void agregarSerie(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // ID de la peli o serie

        // Inserting Row
        db.insert(TABLE_SERIES, null, values);

        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<Integer> getAllIDMovies() {
        List<Integer> lista_ids_peliculas = new ArrayList<Integer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PELIS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                lista_ids_peliculas.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        // return contact list
        return lista_ids_peliculas;
    }

    public List<Integer> getAllIDSeries() {
        List<Integer> lista_ids_peliculas = new ArrayList<Integer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SERIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                lista_ids_peliculas.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        // return contact list
        return lista_ids_peliculas;
    }

    // Deleting single contact
    public void deletePelicula(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PELIS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Deleting single contact
    public void deleteSerie(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERIES, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting contacts Count
    public int getPelisCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PELIS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Getting contacts Count
    public int getSeriesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SERIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public boolean esSerieFavorita(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean es_favorita = false;

        Cursor cursor = db.query(
                TABLE_SERIES /* table */,
                new String[] { "id" } /* columns */,
                "id = ?" /* where or selection */,
                new String[] { String.valueOf(id) }/* selectionArgs i.e. value to replace ? */,
                null /* groupBy */,
                null /* having */,
                null /* orderBy */
        );

        if (cursor.getCount() != 0)
            es_favorita = true;

        return es_favorita;

    }

    public boolean esPeliculaFavorita(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean es_favorita = false;

        Cursor cursor = db.query(
                TABLE_PELIS /* table */,
                new String[] { "id" } /* columns */,
                "id = ?" /* where or selection */,
                new String[] { String.valueOf(id) }/* selectionArgs i.e. value to replace ? */,
                null /* groupBy */,
                null /* having */,
                null /* orderBy */
        );

        if (cursor.getCount() != 0)
            es_favorita = true;

        return es_favorita;

    }
}

