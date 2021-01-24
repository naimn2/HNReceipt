package com.kakzain.hnreceipt.db.lokal;

import java.util.List;

public interface ILokalHelper<E> {
    ILokalHelper<E> setReference(String reference);
    ILokalHelper<E> save(E object);
    ILokalHelper<E> save(List<E> objects);
    ILokalHelper<E> get();
    ILokalHelper<E> getList();
}
