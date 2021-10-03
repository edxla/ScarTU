package com.mobiletracker.scarTU.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperSQLite extends SQLiteOpenHelper {
    public HelperSQLite(Context context) {
        super(context, "ubicacionesInternas.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table ubicaciones23(idUbicacion integer primary key, idConductor TEXT, latitud TEXT, longitud TEXT,hora TEXT,fecha TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists ubicaciones23");
    }

    public Boolean insertarDatos(String idConductor, double latitud, double longitud, String hora, String fecha)
    {
        SQLiteDatabase mSQLiteDB = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put("idConductor", idConductor);
        mContentValues.put("latitud", latitud);
        mContentValues.put("longitud", longitud);
        mContentValues.put("fecha", hora);
        mContentValues.put("hora", fecha);
        long resultado=mSQLiteDB.insert("ubicaciones23", null, mContentValues);
        if(resultado==-1){
            return false;
        }else{
            return true;
        }
    }



    public Boolean eliminarDatos (String idConductor)
    {
        SQLiteDatabase mSQLiteDB = this.getWritableDatabase();
        Cursor cursor = mSQLiteDB.rawQuery("Select * from ubicaciones23 where idConductor = ?", new String[]{idConductor});
        if (cursor.getCount() > 0) {
            long resultado = mSQLiteDB.delete("ubicaciones23", "idConductor=?", new String[]{idConductor});
            if (resultado == -1) {
                return false;
            } else {
                cursor.close(); // * Importante cerrar el cursor creado por cada operación!.
                return true;
            }
        } else {
            return false;
        }


    }

    public Cursor obtenerDatos ()
    {
        SQLiteDatabase mSQLiteDB = this.getWritableDatabase();
        Cursor cursor = mSQLiteDB.rawQuery("Select * from ubicaciones23", null);
        cursor.close(); // * Importante cerrar el cursor creado por cada operación!.
        return cursor;

    }
}
