package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListKaryawanAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.db.lokal.LokalHelper
import com.kakzain.hnreceipt.db.lokal.MyConstants
import com.kakzain.hnreceipt.model.Karyawan
import kotlinx.android.synthetic.main.activity_daftar_karyawan.*

class DaftarKaryawanActivity : AppCompatActivity() {
    private val TAG = DaftarKaryawanActivity::class.java.simpleName
    private lateinit var listKaryawanAdapter: ListKaryawanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_karyawan)

        setSupportActionBar(toolbar_activityDaftarKaryawan)
        title = getString(R.string.daftar_karyawan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_activityDaftarKaryawan.layoutManager = LinearLayoutManager(this)
        listKaryawanAdapter = ListKaryawanAdapter(this)
            .setData(ArrayList<Karyawan>(MyConstants.getAllKaryawan(this).values))
        rv_activityDaftarKaryawan.adapter = listKaryawanAdapter
    }

    fun sinkronClickHandle(view: View) {
        showLoading(true)
        FirestoreHelper<Karyawan>().setReference(Karyawan.KARYAWAN_DB_REFERENCE)
            .addOnceListValuesEventListenerCallback(
                object : IDatabaseHelper.ListValuesEventListenerCallback<Karyawan> {
                override fun onDataUpdate(values: ArrayList<Karyawan>?, ids: ArrayList<String>?) {
                    showLoading(false)
                    if (values != null && ids != null){
                        writeToLocal(values, ids)
                    }
                }

                override fun onListValueEventListenerCancelled(errorMessage: String?) {
                    showLoading(false)
                    Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                }
            }, Karyawan::class.java)
    }

    private fun writeToLocal(values: java.util.ArrayList<Karyawan>, ids: java.util.ArrayList<String>) {
        val lokalKaryawan = LokalHelper<Karyawan>(this, LokalHelper.DB_WHICH_KARYAWAN)
        lokalKaryawan.open()
        for (i: Int in 0 until values.size){
            if (lokalKaryawan.isExist(ids[i])){
                lokalKaryawan.update(ids[i], Karyawan(values[i].nama))
            } else {
                lokalKaryawan.insert(ids[i], Karyawan(values[i].nama))
            }
        }
        lokalKaryawan.close()
        updateDataRecyclerViewAdapter()
    }

    private fun updateDataRecyclerViewAdapter(){
        listKaryawanAdapter.setData(
            ArrayList<Karyawan>(MyConstants.getAllKaryawan(this).values)
        )
    }

    private fun showLoading(b: Boolean){
        if (b){
            pb_activityDaftarKaryawan.visibility = View.VISIBLE
            rv_activityDaftarKaryawan.visibility = View.INVISIBLE
        } else {
            rv_activityDaftarKaryawan.visibility = View.VISIBLE
            pb_activityDaftarKaryawan.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}