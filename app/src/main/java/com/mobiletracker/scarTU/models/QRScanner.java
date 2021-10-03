package com.mobiletracker.scarTU.models;

public class QRScanner {
    public QRScanner(String datos) {
        this.datos = datos;
    }

    public QRScanner() {

    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    String datos;
}
