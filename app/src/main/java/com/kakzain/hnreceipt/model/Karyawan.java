package com.kakzain.hnreceipt.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    public static final String KARYAWAN_DB_REFERENCE = "karyawan";
    private String nama;
    private int posisi; // id_posisi

    public Karyawan() {
    }

    public Karyawan(String nama, int posisi) {
        this.nama = nama;
        this.posisi = posisi;
    }

    public String getNama() {
        return nama;
    }

    public int getPosisi() {
        return posisi;
    }
}
