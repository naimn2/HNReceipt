package com.kakzain.hnreceipt.db.lokal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Lahan;
import com.kakzain.hnreceipt.model.Posisi;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "hndev.db";
    public static final String[] DB_TABLES = new String[]{"karyawan", "lahan", "posisi"};
    public static final int DB_VERSION = 3;
    public static final String _ID = "id";

    private static final String[] SQL_CREATE_TABLES = new String[]{
            String.format("CREATE TABLE %s (" +
                            "%s TEXT PRIMARY KEY, " +
                            "%s TEXT NOT NULL" +
                            ")",
                    DB_TABLES[0],
                    _ID,
                    Karyawan.NAMA_COLUMN),
            String.format("CREATE TABLE %s (" +
                            "%s TEXT PRIMARY KEY, " +
                            "%s TEXT NOT NULL" +
                            ")",
                    DB_TABLES[1],
                    _ID,
                    Lahan.NAMA_LAHAN_COLUMN),
            String.format("CREATE TABLE %s (" +
                            "%s TEXT PRIMARY KEY, " +
                            "%s TEXT NOT NULL" +
                            ")",
                    DB_TABLES[2],
                    _ID,
                    Posisi.NAMA_POSISI_COLUMN)
    };

    public MySqliteOpenHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (int i = 0; i < DB_TABLES.length; i++) {
            sqLiteDatabase.execSQL(SQL_CREATE_TABLES[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (int j = 0; j < DB_TABLES.length; j++) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLES[j]);
        }
        onCreate(sqLiteDatabase);
    }
}
