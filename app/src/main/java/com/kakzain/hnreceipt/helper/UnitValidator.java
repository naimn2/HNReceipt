package com.kakzain.hnreceipt.helper;

public class UnitValidator {
    public static String validateUnitPercent(int num){
        return num+" %";
    }
    public static int unitPercentToInt(String s){
        return Integer.parseInt(s.replaceAll("\\s+", "").split("%")[0]);
    }
    public static String validateUnitCurrency(int num){
        String s = String.valueOf(num);
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            if (i % 3 == 0 && i != 0){
                result = "." + result;
            }
            result = s.charAt(s.length()-1-i) + result;
        }

        return "Rp " + result + ",-";
    }
    public static int unitCurrencytoInt(String s){
        try {
            return Integer.parseInt(s.replaceAll("\\s+", "").split("Rp")[1]
                    .replaceAll("\\.", "").split("-")[0].split(",")[0]);
        } catch (ArrayIndexOutOfBoundsException aie){
            return Integer.parseInt(s.replaceAll("\\s+", "").split("Rp")[0]
                    .replaceAll("\\.", "").split("-")[0].split(",")[0]);
        } catch (Exception e){
            return 0;
        }
    }
}
