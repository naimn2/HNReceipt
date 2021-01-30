package com.kakzain.hnreceipt.model;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class DeliveryOrder{
    private static final String TAG = DeliveryOrder.class.getSimpleName();
    public static final String DO_DB_REFERENCE = "delivery_order";
    public static final String TANGGAL_KEY = "tanggal";
    private static final int HARGA_SAWIT_DEFAULT = 1850;
    private static final float REFAKSI_DEFAULT = 0.04f;

    private List<PanenSawitLahan> panenSawitLahan;
    private List<Kehadiran> sopir;
    private float beratTotal;
    private Timestamp tanggal;
    private int hargaSawit;
    private float refaksi;
    private boolean isCommitted;
//    private int upah;
//    private float upahSopir;
//    private int upahBrondol;
    private List<Upah> upah;

    private List<Object> toList(){
        List<Object> values = new ArrayList<>();
        values.add(panenSawitLahan);
        values.add(sopir);
        values.add(beratTotal);
        values.add(tanggal);
        values.add(hargaSawit);
        values.add(refaksi);
        values.add(isCommitted);
        values.add(upah);
        return values;
    }

    public DeliveryOrder() {
        panenSawitLahan = new ArrayList<>();
    }

    public DeliveryOrder(List<PanenSawitLahan> panenSawitLahan, int hargaSawit, float refaksi,
                         float beratTotal, @Nonnull Date tanggal, List<Kehadiran> sopir, List<Upah> upah) {
        this.panenSawitLahan = panenSawitLahan;
        this.sopir = sopir;
        this.beratTotal = beratTotal;
        this.tanggal = new Timestamp(tanggal);
        this.hargaSawit = hargaSawit !=0 ? hargaSawit : HARGA_SAWIT_DEFAULT;
//        this.upah = upah;
        this.refaksi = refaksi != 0 ? refaksi : REFAKSI_DEFAULT;
        isCommitted = false;
        this.upah = upah;
    }

    public void setPanenSawitLahan(List<PanenSawitLahan> panenSawitLahan) {
        this.panenSawitLahan = panenSawitLahan;
    }

    public void setSopir(List<Kehadiran> sopir) {
        this.sopir = sopir;
    }

    public void setHargaSawit(int hargaSawit) {
        this.hargaSawit = hargaSawit;
    }

    public void setUpah(List<Upah> upah) {
        this.upah = upah;
    }

    public void setRefaksi(float refaksi) {
        this.refaksi = refaksi;
    }

//    public void setUpahSopir(float upahSopir) {
//        this.upahSopir = upahSopir;
//    }
//
//    public void setUpahBrondol(int upahBrondol) {
//        this.upahBrondol = upahBrondol;
//    }

    public void setBeratTotal(float beratTotal) {
        this.beratTotal = beratTotal;
    }

    public void setCommitted(boolean committed) {
        isCommitted = committed;
    }

    public boolean isCommitted() {
        return isCommitted;
    }

//    public float getUpahSopir() {
//        return upahSopir;
//    }
//
//    public int getUpahBrondol() {
//        return upahBrondol;
//    }

    public List<PanenSawitLahan> getPanenSawitLahan() {
        return panenSawitLahan;
    }

    public List<Kehadiran> getSopir() {
        return sopir;
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

    public List<Upah> getUpah() {
        return upah;
    }

    public float getRefaksi() {
        return refaksi;
    }

    public boolean isComplete(){
        for (Object obj: toList()){
            try {
                if (obj == null) {
                    Log.d(TAG, "isComplete: there is a null object");
                    return false;
                } else if (Double.parseDouble(obj.toString()) == 0) {
                    Log.d(TAG, "isComplete: there is a zero number");
                    return false;
                }
            } catch (NumberFormatException ignored){}
        }
        return true;
    }
}
