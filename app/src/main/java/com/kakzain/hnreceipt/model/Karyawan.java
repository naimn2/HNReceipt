package com.kakzain.hnreceipt.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    public static final String KARYAWAN_DB_REFERENCE = "karyawan";
    public static final String NAMA_COLUMN = "nama";

    private String nama;

    public Karyawan() {
    }

    public Karyawan(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }
}
