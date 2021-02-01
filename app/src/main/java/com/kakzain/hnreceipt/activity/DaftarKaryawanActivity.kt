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
import com.kakzain.hnreceipt.adapter.ListKaryawanAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.db.lokal.LokalHelper
import com.kakzain.hnreceipt.db.lokal.MyConstants
import com.kakzain.hnreceipt.model.Karyawan
import kotlinx.android.synthetic.main.activity_daftar_karyawan.*

class DaftarKaryawanActivity : AppCompatActivity() {
    private val TAG = DaftarKaryawanActivity::class.java.simpleName
    private lateinit var lokalKaryawan: LokalHelper<Karyawan>
    private lateinit var listKaryawanAdapter: ListKaryawanAdapter
    private lateinit var dbKaryawanHelper: IDatabaseHelper<Karyawan>
    private lateinit var mapKaryawan: Map<String, Karyawan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_karyawan)

        mapKaryawan = MyConstants.getAllKaryawan(this)
        lokalKaryawan = LokalHelper<Karyawan>(this, LokalHelper.DB_WHICH_KARYAWAN)
        dbKaryawanHelper = FirestoreHelper<Karyawan>()
                .setReference(Karyawan.KARYAWAN_DB_REFERENCE)
                .orderBy(FirestoreHelper.TIMESTAMP_COLUMN, FirestoreHelper.ASCENDING_DIRECTION)
                .addConditional(IDatabaseHelper.IS_DELETED_COLUMN, false)

        setSupportActionBar(toolbar_activityDaftarKaryawan)
        title = getString(R.string.daftar_karyawan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_activityDaftarKaryawan.layoutManager = LinearLayoutManager(this)
        listKaryawanAdapter = ListKaryawanAdapter(this)
            .setData(ArrayList<Karyawan>(mapKaryawan.values))
            .setOnMenuItemClickListenerCallback { _, i ->
                if (mapKaryawan.isNotEmpty()){
                    val id = ArrayList<String>(mapKaryawan.keys)[i]
                    hapusKaryawan(id)
                }
            }
        rv_activityDaftarKaryawan.adapter = listKaryawanAdapter
    }

    fun sinkronClickHandle(view: View) {
        sinkronDataKaryawan()
    }

    private fun sinkronDataKaryawan() {
        showLoading(true)
        dbKaryawanHelper
                .addOnceListValuesEventListenerCallback(
                        object : IDatabaseHelper.ListValuesEventListenerCallback<Karyawan> {
                            override fun onDataUpdate(values: ArrayList<Karyawan>?, ids: ArrayList<String>?) {
                                showLoading(false)
                                if (values != null && ids != null) {
                                    Log.d(TAG, "onDataUpdate: Server size: ${values.size}")
                                    writeToLocal(values, ids)
                                }
                            }

                            override fun onListValueEventListenerCancelled(errorMessage: String?) {
                                showLoading(false)
                                Log.e(TAG, "onListKaryawanEventListenerCancelled: $errorMessage")
                            }
                        }, Karyawan::class.java)
    }

    private fun writeToLocal(values: ArrayList<Karyawan>, ids: ArrayList<String>) {
        lokalKaryawan.open()
        lokalKaryawan.deleteAll()
        for (i: Int in 0 until values.size){
            lokalKaryawan.insert(ids[i], Karyawan(values[i].nama))
        }
        lokalKaryawan.close()
        updateDataRecyclerViewAdapter()
    }

    private fun updateDataRecyclerViewAdapter(){
        mapKaryawan = MyConstants.getAllKaryawan(this)
//        for (s in mapKaryawan.keys){
//            val k = mapKaryawan[s]
//            Log.d(TAG, "updateDataRecyclerViewAdapter: ${k?.nama}")
//        }
        listKaryawanAdapter.setData(ArrayList<Karyawan>(mapKaryawan.values))
        Log.d(TAG, "updateDataRecyclerViewAdapter: Local size: ${mapKaryawan.size}")
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

    fun tambahKaryawan() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_form_input_nama, null, false)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialogFormInputNama_title)
        val etInputNama = dialogView.findViewById<EditText>(R.id.et_dialogFormInputNama_input)
        val btnTambah = dialogView.findViewById<Button>(R.id.btn_dialogFormInputNama_tambah)
        val etTutup = dialogView.findViewById<TextView>(R.id.tv_dialogFormInputNama_tutup)

        val mAlertDialog = AlertDialog.Builder(this).setView(dialogView).create()
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_person_add, 0, 0, 0)
        etInputNama.hint = getString(R.string.nama_karyawan)
        btnTambah.text = getString(R.string.tambah_karyawan)
        btnTambah.setOnClickListener {
            val newKaryawan = Karyawan(etInputNama.text.toString())
            if (TextUtils.isEmpty(etInputNama.text)){
                etInputNama.error = getString(R.string.tidak_boleh_kosong)
                etInputNama.requestFocus()
                return@setOnClickListener
            }
            val id = dbKaryawanHelper.pushWriteValue(newKaryawan)
            dbKaryawanHelper.refChild(id).insertTimestamp()
            mAlertDialog.dismiss()
            sinkronDataKaryawan()
            Toast.makeText(this, getString(R.string.karyawan_ditambah), Toast.LENGTH_SHORT).show()
        }
        etTutup.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mAlertDialog.show()
    }

    private fun hapusKaryawan(id: String) {
        val deletedKaryawan = mapKaryawan[id]
        if (deletedKaryawan != null) {
            deletedKaryawan.isDeleted = true
            dbKaryawanHelper.refChild(id).writeValue(deletedKaryawan)
            lokalKaryawan.open()
            if (lokalKaryawan.isExist(id)) {
                lokalKaryawan.delete(id)
            }
            lokalKaryawan.close()
            updateDataRecyclerViewAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_daftar_karyawan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        } else if (item.itemId == R.id.item_menuToolbarDaftarKaryawan_tambahKaryawan){
            tambahKaryawan()
        }

        return super.onOptionsItemSelected(item)
    }
}
