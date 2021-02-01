package com.kakzain.hnreceipt.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    public static final String KARYAWAN_DB_REFERENCE = "karyawan";
    public static final String NAMA_COLUMN = "nama";

    private String nama;
    private boolean isDeleted;

    public Karyawan() {
        isDeleted = false;
    }

    public Karyawan(String nama) {
        this.nama = nama;
        isDeleted = false;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getNama() {
        return nama;
    }
}
