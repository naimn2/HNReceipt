package com.kakzain.hnreceipt.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOrder{
    public static final String DO_DB_REFERENCE = "delivery_order";

    private List<PanenSawitLahan> panenSawitLahan;
    private float beratTotal;
    private Timestamp tanggal;
    private int hargaSawit;
    private long upah;
    private float refaksi;

    public DeliveryOrder() {
        panenSawitLahan = new ArrayList<>();
    }

    public DeliveryOrder(List<PanenSawitLahan> panenSawitLahan, float beratTotal, Timestamp tanggal, int hargaSawit, long upah, float refaksi) {
        this.panenSawitLahan = panenSawitLahan;
        this.beratTotal = beratTotal;
        this.tanggal = tanggal;
        this.hargaSawit = hargaSawit;
        this.upah = upah;
        this.refaksi = refaksi;
    }

    public void setPanenSawitLahan(List<PanenSawitLahan> panenSawitLahan) {
        this.panenSawitLahan = panenSawitLahan;
    }

    public void setBeratTotal(float beratTotal) {
        this.beratTotal = beratTotal;
    }

    public List<PanenSawitLahan> getPanenSawitLahan() {
        return panenSawitLahan;
    }

    public float getBeratTotal() {
        return beratTotal;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public int getHargaSawit() {
        return hargaSawit;
    }

    public long getUpah() {
        return upah;
    }

    public float getRefaksi() {
        return refaksi;
    }
}
