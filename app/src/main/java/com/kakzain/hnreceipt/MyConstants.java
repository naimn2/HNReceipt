package com.kakzain.hnreceipt;

import android.content.Context;

import com.kakzain.hnreceipt.db.lokal.ILokalHelper;
import com.kakzain.hnreceipt.db.lokal.LokalHelper;
import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Lahan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyConstants { // should be put in local db that synchronized with server db
    public static final String[] POSISI = new String[]{"AKT","PN","SP","BR"};
    public static final int POSISI_PENGANGKUT = 0;
    public static final int POSISI_PEMANEN = 1;
    public static final int POSISI_SOPIR = 2;
    public static final int POSISI_BRONDOL = 3;

    public static List<String> getLahanArrayList(Context context, boolean hint){
        ILokalHelper<Lahan> lokalLahan = new LokalHelper<>(context, LokalHelper.DB_WHICH_LAHAN);
        lokalLahan.open();
        List<String> namaLahanArray = new ArrayList<>();
        if (hint) {
            namaLahanArray.add("Pilih Lahan");
        }
        if (!lokalLahan.isEmpty()){
            Map<String, Lahan> mapLahan = lokalLahan.getItems(Lahan.class);
            for (Lahan lahanObject: mapLahan.values()){
                namaLahanArray.add(lahanObject.getNamaLahan());
            }
        }
        lokalLahan.close();
        return namaLahanArray;
    }

    public static Map<String, Karyawan> getAllKaryawan(Context context){
        ILokalHelper<Karyawan> lokalKaryawan = new LokalHelper<>(context, LokalHelper.DB_WHICH_KARYAWAN);
        lokalKaryawan.open();
        Map<String, Karyawan> karyawanMap = new HashMap<>();
        if (!lokalKaryawan.isEmpty()){
            karyawanMap = lokalKaryawan.getItems(Karyawan.class);
        }
        lokalKaryawan.close();
        return karyawanMap;
    }
}
