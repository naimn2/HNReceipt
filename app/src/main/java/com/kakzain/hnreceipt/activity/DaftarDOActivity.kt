package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListDeliveryOrderAdapter
import com.kakzain.hnreceipt.adapter.ListDeliveryOrderAdapter.LOAD_LIMIT
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.model.DeliveryOrder
import kotlinx.android.synthetic.main.activity_daftar_d_o.*
import java.util.ArrayList

class DaftarDOActivity : AppCompatActivity() {
    private lateinit var deliveryOrderAdapter: ListDeliveryOrderAdapter
    private val TAG = DaftarDOActivity::class.java.simpleName
    private lateinit var dbDOhelper: IDatabaseHelper<DeliveryOrder>
    private var isLoadingMore = false
    private var limit: Long = LOAD_LIMIT
    private var prevLoadedDataLen = 0
    private var isEndOfData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_d_o)

        setSupportActionBar(toolbar_activityDaftarDO)
        title = getString(R.string.daftar_do)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_activityDaftarDO_daftarDO.layoutManager = LinearLayoutManager(this)
        deliveryOrderAdapter = ListDeliveryOrderAdapter(this)
        rv_activityDaftarDO_daftarDO.adapter = deliveryOrderAdapter

        dbDOhelper = FirestoreHelper()
        dbDOhelper.setReference(DeliveryOrder.DO_DB_REFERENCE)
            .orderBy(DeliveryOrder.TANGGAL_KEY, FirestoreHelper.DESCENDING_DIRECTION)
        readDOdb()
        setRecyclerViewScrollListener()
        showLoading(true)
        showNoDataMessage(false)
        showRecyclerView(false)
    }

    private fun setRecyclerViewScrollListener() {
        rv_activityDaftarDO_daftarDO.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && !isLoadingMore && !isEndOfData) {
                    isLoadingMore = true
                    limit += LOAD_LIMIT
                    readDOdb()
                }
            }
        })
    }

    private fun readDOdb() {
        Log.d(TAG, "readDOdb: Limit: $limit")
        dbDOhelper.limit(limit).addOnceListValuesEventListenerCallback(object :
                IDatabaseHelper.ListValuesEventListenerCallback<DeliveryOrder> {
                override fun onDataUpdate(
                    values: ArrayList<DeliveryOrder>?,
                    ids: ArrayList<String>?
                ) {
                    showLoading(false)
                    isLoadingMore = false
                    isEndOfData = false // inisial awal ujung/akhir data

                    if (values != null && ids != null) {
                        deliveryOrderAdapter.setData(values, ids)
                        Log.d(TAG, "onDataUpdate: ${values.size}")
                        if (values.size == 0) {
                            isEndOfData = true
                            showNoDataMessage(true)
                            showRecyclerView(false)
                        } else {
                            showNoDataMessage(false)
                            showRecyclerView(true)
                        }

                        // CEK APAKAH SEMUA DATA SUDAH TER-LOAD (TIDAK ADA LAGI
                        // DATA TERSISA DI DATABASE)
                        if (values.size == prevLoadedDataLen){
                            isEndOfData = true
                        } else {
                            isEndOfData = false
                            prevLoadedDataLen = values.size
                        }
                    } else {
                        isEndOfData = true
                        showNoDataMessage(true)
                        showRecyclerView(false)
                    }

                    // PERBARUI ADAPTER JIKA DI UJUNG DATA
                    deliveryOrderAdapter.setEndOfData(isEndOfData)
                }

                override fun onListValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onListValueEventListenerCancelled: $errorMessage")
                }
            }, DeliveryOrder::class.java)
    }

    private fun showLoading(b: Boolean){
        if (b){
            pb_activityDaftarDO_loading.visibility = View.VISIBLE
        } else {
            pb_activityDaftarDO_loading.visibility = View.GONE
        }
    }

    private fun showRecyclerView(b: Boolean){
        if (!b){
            rv_activityDaftarDO_daftarDO.visibility = View.INVISIBLE
        } else {
            rv_activityDaftarDO_daftarDO.visibility = View.VISIBLE
        }
    }

    private fun showNoDataMessage(b: Boolean){
        if (b){
            tv_activityDaftarDO_noDO.visibility = View.VISIBLE
        } else {
            tv_activityDaftarDO_noDO.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}