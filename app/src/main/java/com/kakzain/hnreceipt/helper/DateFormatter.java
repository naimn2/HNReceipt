package com.kakzain.hnreceipt.helper;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    public static final String[] MONTHS_ID = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String day = DateFormat.format("dd", cal).toString();
        String month = MONTHS_ID[Integer.parseInt(DateFormat.format("MM", cal).toString())-1];
        String year = DateFormat.format("yyyy", cal).toString();
        return day+" "+month+" "+year;
    }
    public static String getTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh.mm");
        String date = simpleDateFormat.format(new Date(time));
        return date;
    }

    public static int getYear(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy", cal).toString();
        return Integer.parseInt(date);
    }

    public static int getMonth(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("MM", cal).toString();
        return Integer.parseInt(date);
    }

    public static int getDay(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd", cal).toString();
        return Integer.parseInt(date);
    }

    public static int getMinute(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("mm", cal).toString();
        return Integer.parseInt(date);
    }
}
