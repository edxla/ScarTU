package com.mobiletracker.scarTU.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CloudFireStoreProvider {

    double latitud;
    double longitud;
    String horaNTP;
    String idConductor;

    FirebaseFirestore mFirestore;


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

    public String getHoraNTP() {
        return horaNTP;
    }

    public void setHoraNTP(String horaNTP) {
        this.horaNTP = horaNTP;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public FirebaseFirestore getmFirestore() {
        return mFirestore;
    }

    public void setmFirestore(FirebaseFirestore mFirestore) {
        this.mFirestore = mFirestore;
    }



    public CloudFireStoreProvider(double latitud, double longitud, String horaNTP, String idConductor) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.horaNTP = horaNTP;
        this.idConductor = idConductor;
    }

    public CloudFireStoreProvider()
    {
        mFirestore = FirebaseFirestore.getInstance();

    }

    //HoraCloud
    public Task<Void> updateCloud (ClockProvider  cloudProvider)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("Hora", cloudProvider.getHoraNTP());
        map.put("latitud", cloudProvider.getLatitud());
        map.put("longitud", cloudProvider.getLongitud());

        return mFirestore.collection("Ubicaciones").document().set(map);

    }


    //
}

