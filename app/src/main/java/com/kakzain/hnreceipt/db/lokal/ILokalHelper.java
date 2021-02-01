package com.kakzain.hnreceipt.db.lokal;

import android.content.Context;

import java.util.Map;

public interface ILokalHelper<E> {
    void open();
    void close();
    long insert(String id, E value);
    Map<String, E> getItems(Class<E> eClass);
    int update(String id, E value);
    int delete(String id);
    boolean isExist(String...id);
    boolean isEmpty();

    void createTable();

    void deleteAll();
}
