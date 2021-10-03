package com.mobiletracker.scarTU.providers;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiletracker.scarTU.models.Client;

import java.util.HashMap;
import java.util.Map;

public class ClockProvider {

    String horaNTP;

    public String getFechaDispositivo() {
        return FechaDispositivo;
    }

    public void setFechaDispositivo(String fechaDispositivo) {
        FechaDispositivo = fechaDispositivo;
    }

    String FechaDispositivo;

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    double latitud;
    double longitud;

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    String idConductor;
    DatabaseReference mDatabase;

    public ClockProvider()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("date_drivers");

    }

    public Task<Void> updateClock (ClockProvider date_drivers)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("Hora", date_drivers.getHoraNTP());
        map.put("latitud", date_drivers.getLatitud());
        map.put("longitud", date_drivers.getLongitud());
        map.put("fecha", date_drivers.getFechaDispositivo());

        return mDatabase.child(date_drivers.getIdConductor()).setValue(map);

    }




    public String getHoraNTP() {
        return horaNTP;
    }

    public void setHoraNTP(String horaNTP) {
        this.horaNTP = horaNTP;
    }


    public ClockProvider (String reference) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(reference);
    }





}
