package com.kakzain.hnreceipt.helper;

import java.util.Date;
import java.util.Random;

public class IDGenerator {
    private static final String PUSH_64KEY = "0123456789-qwertyuiopasdfghjklzxcvbnm_QWERTYUIOPASDFGHJKLZXCVBNM";
    private static long lastTime = 0;

    public static synchronized String generateUniqueKey(long time){
        while (lastTime == time){
            time = new Date().getTime();
        }
        lastTime = time;

        String str = "";
        while (time != 0){
            str = PUSH_64KEY.charAt(Integer.parseInt(""+(time%64))) + str;
            time /= 64;
        }

        Random randGen = new Random();
        for (int i=0; i<13; i++){
            int randI = randGen.nextInt(64);
            str = PUSH_64KEY.charAt(randI) + str;
        }

        return str;
    }
}
