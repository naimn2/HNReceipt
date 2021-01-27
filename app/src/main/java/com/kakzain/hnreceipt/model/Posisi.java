package com.kakzain.hnreceipt.model;

public class Posisi {
    public static final String POSISI_REFERENCE = "posisi";
    public static final String NAMA_POSISI_COLUMN = "namaPosisi";

    private String namaPosisi;

    public Posisi() {
    }

    public Posisi(String namaPosisi) {
        this.namaPosisi = namaPosisi;
    }

    public String getNamaPosisi() {
        return namaPosisi;
    }
}
