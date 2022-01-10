package com.kakzain.hnreceipt.db.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakzain.hnreceipt.db.IDatabaseHelper;
import com.kakzain.hnreceipt.helper.IDGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

public class FirestoreHelper<E> implements IDatabaseHelper<E> {
    private static final String TAG = FirestoreHelper.class.getSimpleName();
    public static final int ASCENDING_DIRECTION = 0;
    public static final int DESCENDING_DIRECTION = 1;
    public static final String TIMESTAMP_COLUMN = "timestamp";
    private Class<E> eClass;
//    private String reference;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private Query queryCollection;
    private ListValuesEventListenerCallback<E> listValuesEventListenerCallback;
    private ValueEventListenerCallback<E> valueEventListenerCallback;
    private ChildEventListenerCallback childEventListenerCallback;
    private final ArrayList<String> keys;

    public FirestoreHelper(){
        keys = new ArrayList<>();
    }

    @Override
    public IDatabaseHelper<E> setReference(String ref) {
        String[] segments = ref.split("/");
        if (segments.length%2 == 0){ // a document reference
            documentReference = FirebaseFirestore.getInstance().document(ref);
        } else { // a collection reference
            collectionReference = FirebaseFirestore.getInstance().collection(ref);
        }
        return this;
    }

    @Override
    public IDatabaseHelper<E> refChild(String child) {
        FirestoreHelper<E> newDbHelper = this;
        String reference = collectionReference.getPath();
        return newDbHelper.setReference(reference+"/"+child);
    }

    @Override
    public IDatabaseHelper<E> addConditional(String key, Object value) {
        if (queryCollection != null){
            queryCollection = queryCollection.whereEqualTo(key, value);
        } else {
            queryCollection = collectionReference.whereEqualTo(key, value);
        }
        return this;
    }

    @Override
    public IDatabaseHelper<E> addExceptCondition(String key, Object value) {
//        exceptConditions.put(key, value);
        if (queryCollection != null){
            queryCollection = queryCollection.whereNotEqualTo(key, value);
        } else {
            queryCollection = collectionReference.whereNotEqualTo(key, value);
        }
        return this;
    }

    @Override
    public IDatabaseHelper<E> addFilter(String key, String query) {
        if (queryCollection != null){
            queryCollection = queryCollection.whereArrayContains(key.toLowerCase(), query.toLowerCase());
        } else {
            queryCollection = collectionReference.whereArrayContains(key.toLowerCase(), query.toLowerCase());
        }
        return this;
    }

    @Override
    public IDatabaseHelper<E> addAllKeys(ArrayList<String> keys) {
        this.keys.addAll(keys);
        return this;
    }

    @Override
    public IDatabaseHelper<E> orderBy(String field, int direction) {
        if (direction == ASCENDING_DIRECTION) {
            if (queryCollection == null)
                queryCollection = collectionReference.orderBy(field, Query.Direction.ASCENDING);
            else
                queryCollection = queryCollection.orderBy(field, Query.Direction.ASCENDING);
        } else if (direction == DESCENDING_DIRECTION){
            if (queryCollection == null)
                queryCollection = collectionReference.orderBy(field, Query.Direction.DESCENDING);
            else
                queryCollection = queryCollection.orderBy(field, Query.Direction.DESCENDING);
        } else {
            try {
                throw new Exception();
            } catch (Exception e) {
                Log.e(TAG, "orderBy: No such direction = "+direction);
            }
        }
        return this;
    }

    @Override
    public IDatabaseHelper<E> limit(long n) {
        if (queryCollection == null)
            queryCollection = collectionReference.limit(n);
        else
            queryCollection = queryCollection.limit(n);
        return this;
    }

    @Override
    public IDatabaseHelper<E> clearConditional() {
        return this;
    }

    @Override
    public IDatabaseHelper<E> clearFilter() {
        return this;
    }

    @Override
    public void addListValuesEventListenerCallback(ListValuesEventListenerCallback<E> listValuesEventListenerCallback, final Class<E> eClass) {
        this.eClass = eClass;
        this.listValuesEventListenerCallback = listValuesEventListenerCallback;
        if (queryCollection != null) {
            queryCollection.addSnapshotListener(new OnCollectionSnapshotListener());
        } else {
            collectionReference.addSnapshotListener(new OnCollectionSnapshotListener());
        }
    }

    @Override
    public void addValueEventListenerCallback(ValueEventListenerCallback<E> valueEventListenerCallback, Class<E> eClass) {
        this.eClass = eClass;
        this.valueEventListenerCallback = valueEventListenerCallback;
        documentReference.addSnapshotListener(new OnDocumentSnapshotListener());
    }

    @Override
    public void addOnceListValuesEventListenerCallback(ListValuesEventListenerCallback<E> listValuesEventListenerCallback, Class<E> eClass) {
        this.eClass = eClass;
        this.listValuesEventListenerCallback = listValuesEventListenerCallback;
        if (queryCollection != null) {
            queryCollection.get().addOnCompleteListener(new OnCollectionCompleteListener());
        } else {
            collectionReference.get().addOnCompleteListener(new OnCollectionCompleteListener());
        }
    }

    @Override
    public void addOnceValueEventListenerCallback(ValueEventListenerCallback<E> valueEventListenerCallback, Class<E> eClass) {
        this.eClass = eClass;
        this.valueEventListenerCallback = valueEventListenerCallback;
        documentReference.get().addOnCompleteListener(new OnDocumentCompleteListener());
    }

    @Override
    public void addChildEventListenerCallback(ChildEventListenerCallback childEventListenerCallback, Class<E> eClass) {

    }

    @Override
    public void writeValue(@NonNull E value) {
        Log.d(TAG, "writeValue: "+documentReference);
        documentReference.set(value);
    }

    @Override
    public String pushWriteValue(@Nonnull E value) {
        String id = IDGenerator.generateUniqueKey(new Date().getTime());
        collectionReference.document(id).set(value);
        return id;
    }

    @Override
    public void delete(){
        if (documentReference!=null) {
            documentReference.delete();
        }
    }

    @Override
    public void close() {
        queryCollection = null;
        collectionReference = null;
        documentReference = null;
        keys.clear();
    }

    private class OnCollectionCompleteListener implements OnCompleteListener<QuerySnapshot> {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){
                // collect document ids
                List<DocumentSnapshot> docSnapshots = task.getResult().getDocuments();
                ArrayList<E> arrayListDocs = new ArrayList<>();
                ArrayList<String> ids = new ArrayList<>();
                for (DocumentSnapshot docSnapshot: docSnapshots){
                    if (!keys.isEmpty() && !keys.contains(docSnapshot.getId())) {
                        continue;
                    }
                    arrayListDocs.add(docSnapshot.toObject(eClass));
                    ids.add(docSnapshot.getId());
                }
                listValuesEventListenerCallback.onDataUpdate(arrayListDocs, ids);
            }
            else {
                listValuesEventListenerCallback.onListValueEventListenerCancelled(task.getException().getMessage());
            }
        }
    }

    private class OnDocumentCompleteListener implements OnCompleteListener<DocumentSnapshot>{
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()){
                E object = task.getResult().toObject(eClass);
                String id = task.getResult().getId();
                valueEventListenerCallback.onDataUpdate(object, id);
            }
            else {
                valueEventListenerCallback.onValueEventListenerCancelled(task.getException().getMessage());
            }
        }
    }

    private class OnCollectionSnapshotListener implements EventListener<QuerySnapshot> {

        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (value != null){
                // collect document ids
                List<DocumentSnapshot> docs = value.getDocuments();
                ArrayList<E> arrayListObject = new ArrayList<>();
                ArrayList<String> ids = new ArrayList<>();
                for (DocumentSnapshot doc: docs){
                    if (!keys.isEmpty() && !keys.contains(doc.getId())) {
                        continue;
                    }
                    arrayListObject.add(doc.toObject(eClass));
                    ids.add(doc.getId());
                }
                listValuesEventListenerCallback.onDataUpdate(arrayListObject, ids);
            }
            else if (error != null){
                listValuesEventListenerCallback.onListValueEventListenerCancelled(error.getMessage());
            }
        }
    }

    private class OnDocumentSnapshotListener implements EventListener<DocumentSnapshot> {

        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            if (value != null){
                E object = value.toObject(eClass);
                String id = value.getId();
                valueEventListenerCallback.onDataUpdate(object, id);
            }
            else if (error != null){
                valueEventListenerCallback.onValueEventListenerCancelled(error.getMessage());
            }
        }
    }

    @Override
    public void insertTimestamp(){
        Map<String,Object> updates = new HashMap<>();
        updates.put(TIMESTAMP_COLUMN, FieldValue.serverTimestamp());
        documentReference.update(updates);
    }
}
