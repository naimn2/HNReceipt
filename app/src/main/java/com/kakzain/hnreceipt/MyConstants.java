package com.kakzain.hnreceipt;

import android.content.Context;
import android.util.Log;

import com.kakzain.hnreceipt.db.lokal.ILokalHelper;
import com.kakzain.hnreceipt.db.lokal.LokalHelper;
import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Lahan;
import com.kakzain.hnreceipt.model.Posisi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyConstants { // should be put in local db that synchronized with server db
    private static final String TAG = MyConstants.class.getSimpleName();
    public static final String[] POSISI = new String[]{"AKT","PN","SP","BR"};

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
            karyawanMap.putAll(lokalKaryawan.getItems(Karyawan.class));
        }
        lokalKaryawan.close();
        return karyawanMap;
    }

    public static Map<Integer, Posisi> getAllPosisi(Context context){
        ILokalHelper<Posisi> lokalPosisi = new LokalHelper<>(context, LokalHelper.DB_WHICH_POSISI);
        lokalPosisi.open();
        Map<Integer, Posisi> resultMap = new HashMap<>();
        if (!lokalPosisi.isEmpty()){
            Map<String, Posisi> posMap = lokalPosisi.getItems(Posisi.class);
            for (String key: posMap.keySet()){
                Posisi pos = posMap.get(key);
                try {
                    resultMap.put(Integer.parseInt(key), pos);
                } catch (NumberFormatException err){
                    Log.e(TAG, "getAllPosisi: "+err.getMessage());
                }
            }
        }
        lokalPosisi.close();
        return resultMap;
    }
}
