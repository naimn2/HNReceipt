package com.kakzain.hnreceipt.model;

import java.util.List;

public class Summary {
    public static final String SUMMARY_REFERENCE = "summary";
    private List<Penggajian> penggajian;
    private float hargaBersih;

    public Summary(List<Penggajian> penggajian, float hargaBersih) {
        this.penggajian = penggajian;
        this.hargaBersih = hargaBersih;
    }

    public List<Penggajian> getPenggajian() {
        return penggajian;
    }

    public float getHargaBersih() {
        return hargaBersih;
    }
}
