package com.kakzain.hnreceipt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.db.lokal.ILokalHelper
import com.kakzain.hnreceipt.db.lokal.LokalHelper
import com.kakzain.hnreceipt.db.lokal.LokalHelper.*
import com.kakzain.hnreceipt.model.Karyawan
import com.kakzain.hnreceipt.model.Lahan
import com.kakzain.hnreceipt.model.Posisi
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var lokalLahanHelper: ILokalHelper<Lahan>
    private lateinit var lokalKaryawanHelper: ILokalHelper<Karyawan>
    private lateinit var lokalPosisiHelper: ILokalHelper<Posisi>

    private lateinit var dbLahanHelper: IDatabaseHelper<Lahan>
    private lateinit var dbKaryawanHelper: IDatabaseHelper<Karyawan>
    private lateinit var dbPosisiHelper: IDatabaseHelper<Posisi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbLahanHelper = FirestoreHelper()
        dbKaryawanHelper = FirestoreHelper()
        dbPosisiHelper = FirestoreHelper()
        lokalLahanHelper = LokalHelper(this, DB_WHICH_LAHAN)
        lokalKaryawanHelper = LokalHelper(this, DB_WHICH_KARYAWAN)
        lokalPosisiHelper = LokalHelper(this, DB_WHICH_POSISI)
    }

    override fun onStart() {
        super.onStart()
        lokalKaryawanHelper.open()
        lokalLahanHelper.open()
        lokalPosisiHelper.open()
        if (lokalKaryawanHelper.isEmpty || lokalLahanHelper.isEmpty || lokalPosisiHelper.isEmpty){
            readDBLahan()
        }
        else {
            Handler().postDelayed(Runnable {
                updateUI()
            }, 2000)
        }
    }

    private fun readDBLahan() {
        dbLahanHelper.setReference(Lahan.LAHAN_REFERENCE)
            .addOnceListValuesEventListenerCallback(object : IDatabaseHelper.ListValuesEventListenerCallback<Lahan> {
                override fun onDataUpdate(values: ArrayList<Lahan>?, ids: ArrayList<String>?) {
                    if (values != null && ids != null) {
                        for (i in 0 until values.size) {
                            if (!lokalLahanHelper.isExist(ids[i])) {
                                lokalLahanHelper.insert(ids[i], values[i])
                            } else {
                                lokalLahanHelper.update(ids[i], values[i])
                            }
                        }
                        Log.d(TAG, "onLahanUpdate: empty? "+lokalLahanHelper.isEmpty)
                    }
                    readDBKaryawan()
                }

                override fun onListValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                }

            }, Lahan::class.java)
    }

    private fun readDBKaryawan() {
        dbKaryawanHelper.setReference(Karyawan.KARYAWAN_DB_REFERENCE)
            .addOnceListValuesEventListenerCallback(object : IDatabaseHelper.ListValuesEventListenerCallback<Karyawan> {
                override fun onDataUpdate(values: ArrayList<Karyawan>?, ids: ArrayList<String>?) {
                    if (values != null && ids != null) {
                        for (i in 0 until values.size) {
                            if (lokalKaryawanHelper.isExist(ids[i])){
                                lokalKaryawanHelper.update(ids[i], values[i])
                            } else {
                                lokalKaryawanHelper.insert(ids[i], values[i])
                            }
                        }
                        Log.d(TAG, "onKaryawanUpdate: empty? "+lokalKaryawanHelper.isEmpty)
                    }
                    readDBPosisi()
                }

                override fun onListValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                }
            }, Karyawan::class.java)
    }

    private fun readDBPosisi() {
        dbPosisiHelper.setReference(Posisi.POSISI_REFERENCE)
            .addOnceListValuesEventListenerCallback(object :
                IDatabaseHelper.ListValuesEventListenerCallback<Posisi> {
                override fun onDataUpdate(values: ArrayList<Posisi>?, ids: ArrayList<String>?) {
                    if (values != null && ids != null) {
                        for (i in 0 until values.size) {
                            if (lokalPosisiHelper.isExist(ids[i])) {
                                lokalPosisiHelper.update(ids[i], values[i])
                            } else {
                                lokalPosisiHelper.insert(ids[i], values[i])
                            }
                        }
                        Log.d(TAG, "onPosisiUpdate: empty? " + lokalPosisiHelper.isEmpty)
                    }
                    updateUI()
                }

                override fun onListValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                }
            }, Posisi::class.java)
    }

    private fun updateUI() {
        val i: Intent
        if (FirebaseAuth.getInstance().currentUser != null) {
            i = Intent(this, HomeActivity::class.java)
        } else {
            i = Intent(this, LoginActivity::class.java)
        }
        startActivity(i)
        finish()
    }

    override fun onStop() {
        super.onStop()
        lokalLahanHelper.close()
        lokalKaryawanHelper.close()
    }
}