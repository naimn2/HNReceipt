package com.kakzain.hnreceipt.model;

public class Penggajian {
    private String idKaryawan;
    private double gaji;

    public Penggajian() {
    }

    public Penggajian(String idKaryawan, double gaji) {
        this.idKaryawan = idKaryawan;
        this.gaji = gaji;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public double getGaji() {
        return gaji;
    }
}
