package com.kakzain.hnreceipt.db.lokal;

import android.content.Context;

import java.util.List;
import java.util.Random;

public class LokalHelper<E> implements ILokalHelper<E>{
    private static final int RANDOM_ID_LENGTH = 200;
    private LokalHelper<E> INSTANCE;
    private String reference;

    public synchronized LokalHelper<E> getInstance(Context context) {
        if (INSTANCE == null){
            return new LokalHelper<>(context);
        } else {
            return INSTANCE;
        }
    }

    private LokalHelper(Context context){
    }

    @Override
    public LokalHelper<E> setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    public LokalHelper<E> save(E object) {
//        Paper.book(reference).write(generateRandomId(RANDOM_ID_LENGTH), object);
        return this;
    }

    @Override
    public LokalHelper<E> save(List<E> objects) {
//        Paper.book(reference).write(generateRandomId(RANDOM_ID_LENGTH), objects);
        return this;
    }

    @Override
    public LokalHelper<E> get() {
        return null;
    }

    @Override
    public LokalHelper<E> getList() {
        return null;
    }

    private String generateRandomId(int length) {
        char[] randomId = new char[length];
        Random random = new Random();
        String result;
//        do {
            for (int i = 0; i < length; i++) {
                int way = random.nextInt(3);
                int randNum;
                switch (way) {
                    case 0:
                        randNum = random.nextInt(10) + 48;
                        break;
                    case 1:
                        randNum = random.nextInt(26) + 65;
                        break;
                    default:
                        randNum = random.nextInt(26) + 97;
                }
                randomId[i] = (char) randNum;
            }
            result = new String(randomId);
//        }
//        while ();
        return result;
    }
}
