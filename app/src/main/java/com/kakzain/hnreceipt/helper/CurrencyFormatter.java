package com.kakzain.hnreceipt.helper;

public class CurrencyFormatter {
    public static String format(double value){
        String[] s = String.valueOf(value).split("\\.");
        String result = "";
        for (int i = 0; i < s[0].length(); i++) {
            if (i % 3 == 0 && i != 0){
                result = "." + result;
            }
            result = s[0].charAt(s[0].length()-1-i) + result;
        }

        return "Rp " + result + ","+s[1];
    }

    public static double unformat(String str){
        // TODO("Not implemented yet")
        return 0.0;
    }
}
