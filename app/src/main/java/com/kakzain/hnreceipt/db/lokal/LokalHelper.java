package com.kakzain.hnreceipt.db.lokal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Lahan;
import com.kakzain.hnreceipt.model.Posisi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.kakzain.hnreceipt.db.lokal.MySqliteOpenHelper.DB_TABLES;
import static com.kakzain.hnreceipt.db.lokal.MySqliteOpenHelper.SQL_CREATE_TABLES;
import static com.kakzain.hnreceipt.db.lokal.MySqliteOpenHelper._ID;

public class LokalHelper<E> implements ILokalHelper<E>{
    public static final int DB_WHICH_KARYAWAN = 0;
    public static final int DB_WHICH_LAHAN = 1;
    public static final int DB_WHICH_POSISI = 2;
    private final MySqliteOpenHelper openHelper;
    private SQLiteDatabase db;
    private final int which;

    public LokalHelper(Context context, int which){
        openHelper = new MySqliteOpenHelper(context, null);
        this.which = which;
    }

    @Override
    public void open() {
        db = openHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        openHelper.close();
        if (db.isOpen()){
            db.close();
        }
    }

    @Override
    public long insert(String id, E value) {
        ContentValues args = new ContentValues();
        args.put(_ID, String.valueOf(id));
        switch (which) {
            case DB_WHICH_KARYAWAN:
                Karyawan karyawan = (Karyawan) value;
                args.put(Karyawan.NAMA_COLUMN, karyawan.getNama());
                break;
            case DB_WHICH_LAHAN:
                Lahan lahan = (Lahan) value;
                args.put(Lahan.NAMA_LAHAN_COLUMN, lahan.getNamaLahan());
                break;
            case DB_WHICH_POSISI:
                Posisi posisi = (Posisi) value;
                args.put(Posisi.NAMA_POSISI_COLUMN, posisi.getNamaPosisi());
                break;
        }
        return db.insert(DB_TABLES[which], null, args);
    }

    @Override
    public Map<String, E> getItems(Class<E> eClass) {
        Cursor cursor = db.query(DB_TABLES[which],null,null,
                null,null,null,null,null);
        cursor.moveToFirst();
        Map<String, E> items = new LinkedHashMap<>();
        if (cursor.getCount() > 0){
            switch (which){
                case DB_WHICH_KARYAWAN:
                    do {
                        Karyawan karyawan = new Karyawan(
                                cursor.getString(cursor.getColumnIndexOrThrow(Karyawan.NAMA_COLUMN))
                        );
                        items.put(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),
                                eClass.cast(karyawan));
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast());
                    break;
                case DB_WHICH_LAHAN:
                    do {
                        Lahan lahan = new Lahan(
                                cursor.getString(cursor.getColumnIndexOrThrow(Lahan.NAMA_LAHAN_COLUMN))
                        );
                        items.put(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),
                                eClass.cast(lahan));
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast());
                    break;
                case DB_WHICH_POSISI:
                    do {
                        Posisi posisi = new Posisi(
                                cursor.getString(cursor.getColumnIndexOrThrow(Posisi.NAMA_POSISI_COLUMN))
                        );
                        items.put(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),
                                eClass.cast(posisi));
                        cursor.moveToNext();
                    } while (!cursor.isAfterLast());
                    break;
            }
        }
        return items;
    }

    @Override
    public int update(String id, E value) {
        ContentValues args = new ContentValues();
        switch (which){
            case DB_WHICH_KARYAWAN:
                Karyawan karyawan = (Karyawan) value;
                args.put(Karyawan.NAMA_COLUMN, karyawan.getNama());
                break;
            case DB_WHICH_LAHAN:
                Lahan lahan = (Lahan) value;
                args.put(Lahan.NAMA_LAHAN_COLUMN, lahan.getNamaLahan());
                break;
            case DB_WHICH_POSISI:
                Posisi posisi = (Posisi) value;
                args.put(Posisi.NAMA_POSISI_COLUMN, posisi.getNamaPosisi());
                break;
        }
        return db.update(DB_TABLES[which], args, _ID+"='" +
                id + "'", null);
    }

    @Override
    public int delete(String id) {
        return db.delete(DB_TABLES[which], _ID + " = '" +
                id + "'", null);
    }

    @Override
    public boolean isExist(String...id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM "+DB_TABLES[which]+" WHERE "+_ID+" = ?",
                id);
        return cursor.moveToFirst();
    }

    @Override
    public boolean isEmpty(){
        Cursor cursor = db.query(DB_TABLES[which], null, null, null,
                null, null, null, null);
        return !cursor.moveToFirst();
    }

    @Override
    public void deleteAll(){
        db.execSQL("delete from "+ DB_TABLES[which]);
    }

    @Override
    public void createTable(){
        db.execSQL(SQL_CREATE_TABLES[which]);
    }
}
