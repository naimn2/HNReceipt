package com.kakzain.hnreceipt.model;

public class Kehadiran {
    private String idKaryawan;
    private int posisi;

    public Kehadiran() {
    }

    public Kehadiran(String idKaryawan, int posisi) {
        this.idKaryawan = idKaryawan;
        this.posisi = posisi;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public int getPosisi() {
        return posisi;
    }
}
