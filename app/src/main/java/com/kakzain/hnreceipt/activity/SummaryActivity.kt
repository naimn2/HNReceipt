package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.model.DeliveryOrder
import com.kakzain.hnreceipt.model.Summary

class SummaryActivity : AppCompatActivity() {
    companion object {
        const val ID_D_O_EXTRA = "summaryIdExtra"
    }
    private val TAG = SummaryActivity::class.java.simpleName

    private lateinit var summary: Summary
    private lateinit var idDO: String
    private lateinit var _DO: DeliveryOrder
    private lateinit var dbSummaryHelper: IDatabaseHelper<Summary>
    private lateinit var dbDOHelper: IDatabaseHelper<DeliveryOrder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        idDO = intent.getStringExtra(ID_D_O_EXTRA)!!
        dbSummaryHelper = FirestoreHelper()
        dbSummaryHelper.setReference(Summary.SUMMARY_DB_REFERENCE+"/"+idDO)
        dbDOHelper = FirestoreHelper()
        dbDOHelper.setReference(DeliveryOrder.DO_DB_REFERENCE+"/"+idDO)

        readSummaryDb()
    }

    private fun readSummaryDb() {
        dbSummaryHelper.addOnceValueEventListenerCallback(
            object : IDatabaseHelper.ValueEventListenerCallback<Summary> {
                override fun onDataUpdate(value: Summary?, id: String?) {
                    if (value != null) {
                        summary = value
                        TODO("on read summary db: set view")
                        readDODb()
                    }
                }

                override fun onValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onValueEventListenerCancelled: $errorMessage")
                }
            }, Summary::class.java
        )
    }

    private fun readDODb() {
        dbDOHelper.addOnceValueEventListenerCallback(
            object : IDatabaseHelper.ValueEventListenerCallback<DeliveryOrder> {
                override fun onDataUpdate(value: DeliveryOrder?, id: String?) {
                    if (value != null) {
                        TODO("on read DO db: update view")
                    }
                }

                override fun onValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onValueEventListenerCancelled: $errorMessage")
                }
            }, DeliveryOrder::class.java
        )
    }
}