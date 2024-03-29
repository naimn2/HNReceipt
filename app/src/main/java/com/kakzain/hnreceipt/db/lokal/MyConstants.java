package com.kakzain.hnreceipt.db.lokal;

import android.content.Context;
import android.util.Log;

import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Lahan;
import com.kakzain.hnreceipt.model.Posisi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MyConstants {
    private static final String TAG = MyConstants.class.getSimpleName();
    public static final int POSISI_PENGANGKUT_INDEKS = 0;
    public static final int POSISI_PEMANEN_INDEKS = 1;
    public static final int POSISI_SOPIR_INDEKS = 2;
    public static final int POSISI_BRONDOL_INDEKS = 3;
    public static final int MAP_KEY_HINT = -1;

    public static Map<Integer, String> getNamaLahanAndIdMap(@Nonnull Context context, @Nullable String hint){
        ILokalHelper<Lahan> lokalLahan = new LokalHelper<>(context, LokalHelper.DB_WHICH_LAHAN);
        lokalLahan.open();
        Map<Integer, String> mapResult = new LinkedHashMap<>();
        if (hint != null) {
            mapResult.put(MAP_KEY_HINT, hint);
        }
        if (!lokalLahan.isEmpty()){
            Map<String, Lahan> mapLahan = lokalLahan.getItems(Lahan.class);
            for (String key: mapLahan.keySet()){
                Lahan lahanObject = mapLahan.get(key);
                if (lahanObject != null) {
                    mapResult.put(Integer.valueOf(key), lahanObject.getNamaLahan());
                }
            }
        }
        lokalLahan.close();
        return mapResult;
    }

    public static Map<String, Karyawan> getAllKaryawan(@Nonnull Context context){
        ILokalHelper<Karyawan> lokalKaryawan = new LokalHelper<>(context, LokalHelper.DB_WHICH_KARYAWAN);
        lokalKaryawan.open();
        Map<String, Karyawan> karyawanMap = new LinkedHashMap<>();
        if (!lokalKaryawan.isEmpty()){
            karyawanMap = lokalKaryawan.getItems(Karyawan.class);
        }
        lokalKaryawan.close();
        Log.d(TAG, "getAllKaryawan: size: "+karyawanMap.size());
        return karyawanMap;
    }

    public static Map<String, Lahan> getAllLahan(@Nonnull Context context){
        ILokalHelper<Lahan> lokalLahan = new LokalHelper<>(context, LokalHelper.DB_WHICH_LAHAN);
        lokalLahan.open();
        Map<String, Lahan> mapLahan = new LinkedHashMap<>();
        if (!lokalLahan.isEmpty()){
            mapLahan = lokalLahan.getItems(Lahan.class);
        }
        lokalLahan.close();
        Log.d(TAG, "getAllLahan: size: "+mapLahan.size());
        return mapLahan;
    }

    public static Map<Integer, Posisi> getAllPosisi(@Nonnull Context context){
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

    public static Map<String, Integer> getNamaPosisiIndeks(@Nonnull Context context){
        Map<Integer, Posisi> mapPosisi = getAllPosisi(context);
        Map<String, Integer> mapResult = new HashMap<>();
        for (Integer i: mapPosisi.keySet()){
            Posisi posisi = mapPosisi.get(i);
            mapResult.put(posisi.getNamaPosisi(), i);
        }
        return mapResult;
    }

    public static Map<Integer, String> getIdDanNamaPosisi(@Nonnull Context context, @Nullable String hint, int notEqual){
        ILokalHelper<Posisi> lokalPosisi = new LokalHelper<>(context, LokalHelper.DB_WHICH_POSISI);
        lokalPosisi.open();
        Map<Integer, String> mapResult = new LinkedHashMap<>();
        if (hint != null){
            mapResult.put(MAP_KEY_HINT, hint);
        }
        if (!lokalPosisi.isEmpty()){
            Map<String, Posisi> posMap = lokalPosisi.getItems(Posisi.class);
            int i = 0;
            for (String key: posMap.keySet()){
                Posisi posisi = posMap.get(key);
                if (i == notEqual){
                    i++;
                    continue;
                }
                mapResult.put(Integer.valueOf(key), posisi.getNamaPosisi());
                i++;
            }
        }
        lokalPosisi.close();
        return mapResult;
    }

//    public static Map<Integer, Posisi> getAllPosisi(@Nonnull Context context, int lessOrEqual){
//        ILokalHelper<Posisi> lokalPosisi = new LokalHelper<>(context, LokalHelper.DB_WHICH_POSISI);
//        lokalPosisi.open();
//        Map<Integer, Posisi> resultMap = new HashMap<>();
//        if (!lokalPosisi.isEmpty()){
//            Map<String, Posisi> posMap = lokalPosisi.getItems(Posisi.class);
//            for (String key: posMap.keySet()){
//                Posisi pos = posMap.get(key);
//                try {
//                    Integer intKey = Integer.parseInt(key);
//                    if (intKey <= lessOrEqual) {
//                        resultMap.put(intKey, pos);
//                    }
//                } catch (NumberFormatException err){
//                    Log.e(TAG, "getAllPosisi: "+err.getMessage());
//                }
//            }
//        }
//        lokalPosisi.close();
//        return resultMap;
//    }
}
