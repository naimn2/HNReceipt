package com.kakzain.hnreceipt.model;

import org.jetbrains.annotations.Nullable;

public class Lahan {
    public static final String NAMA_LAHAN_COLUMN = "namaLahan";
    public static final String LAHAN_DB_REFERENCE = "lahan";

    private String namaLahan;
    private boolean isDeleted;

    public Lahan() {
        isDeleted = false;
    }

    public Lahan(String namaLahan) {
        this.namaLahan = namaLahan;
        isDeleted = false;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getNamaLahan() {
        return namaLahan;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
