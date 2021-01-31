package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListGajiKaryawanAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.db.lokal.MyConstants
import com.kakzain.hnreceipt.helper.UnitValidator
import com.kakzain.hnreceipt.model.DeliveryOrder
import com.kakzain.hnreceipt.model.Summary
import kotlinx.android.synthetic.main.activity_summary.*
import kotlin.math.roundToInt

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
    private lateinit var listGajiKaryawanAdapter: ListGajiKaryawanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        setSupportActionBar(toolbar_activitySummary)
        title = getString(R.string.summary_d_o)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        idDO = intent.getStringExtra(ID_D_O_EXTRA)!!
        dbSummaryHelper = FirestoreHelper()
        dbSummaryHelper.setReference(Summary.SUMMARY_DB_REFERENCE+"/"+idDO)
        dbDOHelper = FirestoreHelper()
        dbDOHelper.setReference(DeliveryOrder.DO_DB_REFERENCE+"/"+idDO)

        rv_activity_summary.layoutManager = object: LinearLayoutManager(this){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        listGajiKaryawanAdapter = ListGajiKaryawanAdapter(this)
        rv_activity_summary.adapter = listGajiKaryawanAdapter
        readSummaryDb()
    }

    private fun readSummaryDb() {
        dbSummaryHelper.addOnceValueEventListenerCallback(
            object : IDatabaseHelper.ValueEventListenerCallback<Summary> {
                override fun onDataUpdate(value: Summary?, id: String?) {
                    if (value != null) {
                        summary = value
                        if (summary.penggajian != null) {
                            listGajiKaryawanAdapter.setData(summary.penggajian)
                        }
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
                        _DO = value
                        setupViews()
                    }
                }

                override fun onValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onValueEventListenerCancelled: $errorMessage")
                }
            }, DeliveryOrder::class.java
        )
    }

    private fun setupViews() {
        val beratKotor = _DO.beratTotal
        val refaksiPercent = (_DO.refaksi*100).roundToInt()
        val beratBersih = beratKotor - (beratKotor*refaksiPercent/100.0)
        val hargaOmzet = beratBersih*_DO.hargaSawit
        val hargaBersih = summary.hargaBersih.toInt()

        var totalGaji = 0.0
        for (penggajian in summary.penggajian){
            totalGaji += penggajian.gaji
        }
        tv_activitySummary_nilaiTotalGaji.text = UnitValidator.validateUnitCurrency(totalGaji.toLong())
        tv_activitySummary_nilaiBeratKotor.text = UnitValidator.validateUnitWeight(beratKotor.toDouble())
        tv_activitySummary_nilaiRefaksi.text = UnitValidator.validateUnitPercent(refaksiPercent)
        tv_activitySummary_nilaiBeratBersih.text = UnitValidator.validateUnitWeight(beratBersih)
        tv_activitySummary_nilaiHargaOmzet.text = UnitValidator.validateUnitCurrency(hargaOmzet.toLong())
        tv_activitySummary_nilaiHargaBersih.text = UnitValidator.validateUnitCurrency(hargaBersih)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}