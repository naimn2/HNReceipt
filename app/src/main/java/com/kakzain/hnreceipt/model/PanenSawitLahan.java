package com.kakzain.hnreceipt.model;

import java.util.List;

public class PanenSawitLahan {
    private int idLahan;
    private List<Kehadiran> kehadiran;
    private float beratBersih;
    private float beratBrondol;

    public PanenSawitLahan(int idLahan, List<Kehadiran> kehadiran, float beratBersih, float beratBrondol) {
        this.idLahan = idLahan;
        this.kehadiran = kehadiran;
        this.beratBersih = beratBersih;
        this.beratBrondol = beratBrondol;
    }

    public PanenSawitLahan() {
    }

    public int getIdLahan() {
        return idLahan;
    }

    public List<Kehadiran> getKehadiran() {
        return kehadiran;
    }

    public float getBeratBersih() {
        return beratBersih;
    }

    public float getBeratBrondol() {
        return beratBrondol;
    }
}
