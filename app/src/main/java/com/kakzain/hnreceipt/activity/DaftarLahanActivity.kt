package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListLahanAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.db.lokal.LokalHelper
import com.kakzain.hnreceipt.db.lokal.MyConstants
import com.kakzain.hnreceipt.model.Lahan
import kotlinx.android.synthetic.main.activity_daftar_lahan.*

class DaftarLahanActivity : AppCompatActivity() {
    private val TAG = DaftarLahanActivity::class.java.simpleName
    private lateinit var lokalLahan: LokalHelper<Lahan>
    private lateinit var listLahanAdapter: ListLahanAdapter
    private lateinit var dbLahanHelper: IDatabaseHelper<Lahan>
    private lateinit var mapLahan: Map<String, Lahan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_lahan)

        mapLahan = MyConstants.getAllLahan(this)
        lokalLahan = LokalHelper<Lahan>(this, LokalHelper.DB_WHICH_LAHAN)
        dbLahanHelper = FirestoreHelper<Lahan>()
                .setReference(Lahan.LAHAN_DB_REFERENCE)
                .orderBy(FirestoreHelper.TIMESTAMP_COLUMN, FirestoreHelper.ASCENDING_DIRECTION)
                .addConditional(IDatabaseHelper.IS_DELETED_COLUMN, false)

        setSupportActionBar(toolbar_activityDaftarLahan)
        title = getString(R.string.daftar_lahan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_activityDaftarLahan.layoutManager = LinearLayoutManager(this)
        listLahanAdapter = ListLahanAdapter(this)
                .setData(ArrayList<Lahan>(mapLahan.values))
                .setOnMenuItemClickListenerCallback { _, i ->
                    if (mapLahan.isNotEmpty()){
                        val id = ArrayList<String>(mapLahan.keys)[i]
                        hapusLahan(id)
                    }
                }
        rv_activityDaftarLahan.adapter = listLahanAdapter
    }

    fun sinkronClickHandle(view: View) {
        sinkronDataLahan()
    }

    private fun sinkronDataLahan() {
        showLoading(true)
        dbLahanHelper
                .addOnceListValuesEventListenerCallback(
                        object : IDatabaseHelper.ListValuesEventListenerCallback<Lahan> {
                            override fun onDataUpdate(values: ArrayList<Lahan>?, ids: ArrayList<String>?) {
                                showLoading(false)
                                if (values != null && ids != null) {
                                    Log.d(TAG, "onDataUpdate: Server size: ${values.size}")
                                    writeToLocal(values, ids)
                                }
                            }

                            override fun onListValueEventListenerCancelled(errorMessage: String?) {
                                showLoading(false)
                                Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                            }
                        }, Lahan::class.java)
    }

    private fun writeToLocal(values: ArrayList<Lahan>, ids: ArrayList<String>) {
        lokalLahan.open()
        lokalLahan.deleteAll()
        Log.d(TAG, "writeToLocal: values size ${values.size}")
        Log.d(TAG, "writeToLocal: ids size ${ids.size}")
        for (i: Int in 0 until values.size){
            lokalLahan.insert(ids[i], Lahan(values[i].namaLahan))
        }
        lokalLahan.close()
        updateDataRecyclerViewAdapter()
    }

    private fun updateDataRecyclerViewAdapter(){
        mapLahan = MyConstants.getAllLahan(this)
//        for (s in mapLahan.keys){
//            val k = mapLahan[s]
//            Log.d(TAG, "updateDataRecyclerViewAdapter: ${k?.namaLahan}")
//        }
        listLahanAdapter.setData(ArrayList<Lahan>(mapLahan.values))
        Log.d(TAG, "updateDataRecyclerViewAdapter: Local size: ${mapLahan.size}")
    }

    private fun showLoading(b: Boolean){
        if (b){
            pb_activityDaftarLahan.visibility = View.VISIBLE
            rv_activityDaftarLahan.visibility = View.INVISIBLE
        } else {
            rv_activityDaftarLahan.visibility = View.VISIBLE
            pb_activityDaftarLahan.visibility = View.GONE
        }
    }

    fun tambahLahan() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_form_input_nama, null, false)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialogFormInputNama_title)
        val etInputNama = dialogView.findViewById<EditText>(R.id.et_dialogFormInputNama_input)
        val btnTambah = dialogView.findViewById<Button>(R.id.btn_dialogFormInputNama_tambah)
        val etTutup = dialogView.findViewById<TextView>(R.id.tv_dialogFormInputNama_tutup)

        val mAlertDialog = AlertDialog.Builder(this).setView(dialogView).create()
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_person_add, 0, 0, 0)
        etInputNama.hint = getString(R.string.nama_lahan)
        btnTambah.text = getString(R.string.tambah_lahan)
        btnTambah.setOnClickListener {
            val newLahan = Lahan(etInputNama.text.toString())
            if (TextUtils.isEmpty(etInputNama.text)){
                etInputNama.error = getString(R.string.tidak_boleh_kosong)
                etInputNama.requestFocus()
                return@setOnClickListener
            }
            writeLahanToDB(newLahan)
            mAlertDialog.dismiss()
        }
        etTutup.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mAlertDialog.show()
    }

    private fun writeLahanToDB(newLahan: Lahan) {
        showLoading(true)
        dbLahanHelper.addOnceListValuesEventListenerCallback(object : IDatabaseHelper.ListValuesEventListenerCallback<Lahan> {
            override fun onDataUpdate(values: ArrayList<Lahan>?, ids: ArrayList<String>?) {
                showLoading(false)
                if (values != null && ids != null) {
                    var id : String
                    if (ids.size == 0) {
                        id = "0"
                    } else {
                        id = (ids[values.size-1].toInt()+1).toString()
                    }
                    dbLahanHelper.refChild(id.toString()).writeValue(newLahan)
                    dbLahanHelper.refChild(id.toString()).insertTimestamp()
                    values.add(newLahan)
                    ids.add(id.toString())
                    writeToLocal(values, ids)
                    Toast.makeText(applicationContext, getString(R.string.lahan_ditambah), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onListValueEventListenerCancelled(errorMessage: String?) {
                showLoading(false)
                Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
            }
        }, Lahan::class.java)
    }

    private fun hapusLahan(id: String) {
        val deletedLahan = mapLahan[id]
        if (deletedLahan != null) {
            deletedLahan.isDeleted = true
            dbLahanHelper.refChild(id).writeValue(deletedLahan)
            lokalLahan.open()
            if (lokalLahan.isExist(id)) {
                lokalLahan.delete(id)
            }
            lokalLahan.close()
            updateDataRecyclerViewAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_daftar_lahan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        else if (item.itemId == R.id.item_menuToolbarDaftarLahan_tambahLahan){
            tambahLahan()
        }

        return super.onOptionsItemSelected(item)
    }
}
