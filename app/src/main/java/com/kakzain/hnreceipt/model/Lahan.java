package com.kakzain.hnreceipt.model;

import org.jetbrains.annotations.Nullable;

public class Lahan {
    public static final String NAMA_LAHAN_COLUMN = "namaLahan";
    public static final String LAHAN_REFERENCE = "lahan";

    private String namaLahan;

    public Lahan() {
    }

    public Lahan(String namaLahan) {
        this.namaLahan = namaLahan;
    }

    public String getNamaLahan() {
        return namaLahan;
    }
}
