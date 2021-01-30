package com.kakzain.hnreceipt.model;

public class Penggajian {
    private String idKaryawan;
    private Double gaji;

    public Penggajian() {
    }

    public Penggajian(String idKaryawan, Double gaji) {
        this.idKaryawan = idKaryawan;
        this.gaji = gaji;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public Double getGaji() {
        return gaji;
    }
}
