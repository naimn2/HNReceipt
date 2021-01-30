package com.kakzain.hnreceipt.model;

public class Upah {
    private int posisi;
    private float upah;

    public Upah() {
    }

    public Upah(int posisi, float upah) {
        this.posisi = posisi;
        this.upah = upah;
    }

    public int getPosisi() {
        return posisi;
    }

    public float getUpah() {
        return upah;
    }
}
