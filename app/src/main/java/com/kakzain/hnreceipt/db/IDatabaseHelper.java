package com.kakzain.hnreceipt.db;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public interface IDatabaseHelper<E> {
    @Nullable
    String IS_DELETED_COLUMN = "deleted";

    IDatabaseHelper<E> setReference(String ref);
    IDatabaseHelper<E> refChild(String child);
//    public IDatabaseHelper<E> setListValuesEventListenerCallback(ListValuesEventListenerCallback<E> listValuesEventListenerCallback);
//    public IDatabaseHelper<E> setValueEventListenerCallback(ValueEventListenerCallback<E> valueEventListenerCallback);
    IDatabaseHelper<E> addConditional(String key, Object value);
    IDatabaseHelper<E> addExceptCondition(String key, Object value);
    IDatabaseHelper<E> addFilter(String key, String query);
    IDatabaseHelper<E> addAllKeys(ArrayList<String> keys);
    IDatabaseHelper<E> orderBy(String field, int direction);
    IDatabaseHelper<E> limit(long n);
    IDatabaseHelper<E> clearConditional();
    IDatabaseHelper<E> clearFilter();
    void addListValuesEventListenerCallback(ListValuesEventListenerCallback<E> listValuesEventListenerCallback, Class<E> eClass);
    void addValueEventListenerCallback(ValueEventListenerCallback<E> valueEventListenerCallback, Class<E> eClass);
    void addOnceListValuesEventListenerCallback(ListValuesEventListenerCallback<E> listValuesEventListenerCallback, Class<E> eClass);
    void addOnceValueEventListenerCallback(ValueEventListenerCallback<E> valueEventListenerCallback, Class<E> eClass);
    void addChildEventListenerCallback(ChildEventListenerCallback childEventListenerCallback, Class<E> eClass);
    void writeValue(@NonNull E value);
    String pushWriteValue(@Nonnull E value);
    void delete();
    void close();
    void insertTimestamp();

    interface ListValuesEventListenerCallback<E> {
        void onDataUpdate(ArrayList<E> values, ArrayList<String> ids);
        void onListValueEventListenerCancelled(String errorMessage);
    }

    interface ChildEventListenerCallback {
        void onChildChanged(String key, Object value);
    }

    interface ValueEventListenerCallback<E> {
        void onDataUpdate(E value, String id);
        void onValueEventListenerCancelled(String errorMessage);
    }
}
