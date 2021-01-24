package com.kakzain.hnreceipt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListKaryawamAdapter
import com.kakzain.hnreceipt.adapter.ListPanenLahanAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.helper.DateFormatter
import com.kakzain.hnreceipt.model.DeliveryOrder
import com.kakzain.hnreceipt.model.Karyawan
import com.kakzain.hnreceipt.model.PanenSawitLahan
import kotlinx.android.synthetic.main.activity_create_d_o.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.ArrayList

class CreateDOActivity : AppCompatActivity() {
    companion object { val ID_DO_EXTRA = "idDOExtra" }

    private val TAG = CreateDOActivity::class.java.simpleName
    private lateinit var dbKaryawanHelper: IDatabaseHelper<Karyawan>
    private lateinit var dbDOHelper: IDatabaseHelper<DeliveryOrder>
    private lateinit var dbPanenSawitLahan: IDatabaseHelper<PanenSawitLahan>
    private lateinit var adapterKehadiran: ListKaryawamAdapter
    private lateinit var panenLahanAdapter: ListPanenLahanAdapter
    private lateinit var listKaryawan: ArrayList<Karyawan>
    private lateinit var listIdKaryawan: ArrayList<String>
    private lateinit var listKehadiran: HashMap<Int, String>
    private lateinit var currentDO: DeliveryOrder
    private var idDO: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_d_o)

        dbKaryawanHelper = FirestoreHelper()
        dbDOHelper = FirestoreHelper()
        dbPanenSawitLahan = FirestoreHelper()
        listKaryawan = ArrayList()
        listIdKaryawan = ArrayList()
        listKehadiran = HashMap()
        idDO = intent.getStringExtra(ID_DO_EXTRA)

        btn_activityCreateDO_tambahLahanPanen.setOnClickListener {
            showDialogTambahLahanPanen()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        panenLahanAdapter = ListPanenLahanAdapter(this)
        rv_activityCreateDO_listLahanPanen.adapter = panenLahanAdapter
        rv_activityCreateDO_listLahanPanen.layoutManager = linearLayoutManager
        readDOdb()
    }

    private fun readDOdb(){
        // BACA DATA DO SEBELUM UPDATE VIEW
        dbDOHelper.setReference(DeliveryOrder.DO_DB_REFERENCE+"/"+idDO).addValueEventListenerCallback(
            object : IDatabaseHelper.ValueEventListenerCallback<DeliveryOrder> {
                override fun onDataUpdate(value: DeliveryOrder?, id: String?) {
                    if (value != null){
                        currentDO = value

                        // UPDATE VIEW
                        tv_activityCreateDO_beratTotal.text = String.format("%.01f KG", value.beratTotal)
                        tv_activityCreateDO_tanggal.text = DateFormatter.getDate(value.tanggal.toDate().time)
                        // SHOW BTN ADD LAHAN PANEN
                        btn_activityCreateDO_tambahLahanPanen.visibility = View.VISIBLE
                        if (value.panenSawitLahan != null && value.panenSawitLahan.size > 0) {
                            // UPDATE LIST PANEN LAHAN ADAPTER
                            panenLahanAdapter.setData(value.panenSawitLahan)
                            // HIDE NO LAHAN PANEN
                            tv_activityCreateDO_noLahanPanen.visibility = View.INVISIBLE
                        } else {
                            // SHOW NO LAHAN PANEN
                            tv_activityCreateDO_noLahanPanen.visibility = View.VISIBLE
                        }
                    }
                }
                override fun onValueEventListenerCancelled(errorMessage: String?) {
                    Log.e(TAG, "onValueEventListenerCancelled: $errorMessage")
                }

            }, DeliveryOrder::class.java)
    }

    private fun showDialogTambahLahanPanen(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_lahan_panen, null, false)

        val btnSave = dialogView.findViewById<Button>(R.id.btn_dialogTambahLahanPanen_save)
        val spinLahan = dialogView.findViewById<Spinner>(R.id.spinner_dialogTambahLahanPanen_lahan)
        val spinArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("Pilih Lahan Sawit", "Lahan1", "Lahan2", "Lahan3"))
        spinLahan.adapter = spinArrayAdapter
        val etBeratBersih = dialogView.findViewById<EditText>(R.id.et_dialogTambahLahanPanen_beratBersih)
        val etBeratBrondol = dialogView.findViewById<EditText>(R.id.et_dialogTambahLahanPanen_beratBrondol)
        val rvKehadiran = dialogView.findViewById<RecyclerView>(R.id.rv_dialogTambahLahanPanen_kehadiranKaryawan)
        val tvTutup = dialogView.findViewById<TextView>(R.id.tv_dialogTambahLahanPanen_tutup)

        // SIAPKAN RECYCLER VIEW KEHADIRAN KARYAWAN
        val linearLayoutManager = LinearLayoutManager(this)
        adapterKehadiran = ListKaryawamAdapter(this)
        adapterKehadiran.setOnHadirListenerCallback { b, position ->
            if (b) {
                listKehadiran.put(position, listIdKaryawan.get(position))
            } else {
                listKehadiran.remove(position)
            }
        }
        rvKehadiran.layoutManager = linearLayoutManager
        rvKehadiran.adapter = adapterKehadiran

        // TAMPILKAN ALERT DIALOG TAMBAH LAHAN PANEN
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).show()

        tvTutup.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener {
            val lahan = spinLahan.selectedItemPosition-1

            // CEK APAKAH BELUM PILIH LAHAN
            if (lahan == -1){
                spinLahan.requestFocus()
                Toast.makeText(this, "Pilih Lahan Sawit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // SIAPKAN MODEL PANEN LAHAN SAWIT UNTUK DITULIS KE DB
                val beratBersih = etBeratBersih.text.toString().toFloat()
                val beratBrondol = etBeratBrondol.text.toString().toFloat()
                val kehadiran = ArrayList(listKehadiran.values)
                saveToDb(PanenSawitLahan(lahan, kehadiran, beratBersih, beratBrondol))
                alertDialog.dismiss()
            }
            catch (numberFormatException: NumberFormatException){
                Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
            }
            catch (exc: Exception){
                Toast.makeText(this, "Error tidak dikenal", Toast.LENGTH_SHORT).show()
            }
        }

        readKaryawanDb()
    }

    private fun saveToDb(lahanPanen: PanenSawitLahan){
        if (currentDO.panenSawitLahan == null) {
            currentDO.panenSawitLahan = ArrayList()
        }
        currentDO.panenSawitLahan.add(lahanPanen)

        // PERBARUI BERAT TOTAL DO
        currentDO.beratTotal += lahanPanen.beratBersih + lahanPanen.beratBrondol

        // TULIS DO TERBARU KE DB
        dbDOHelper.writeValue(currentDO)

        // CLEAR LIST KEHADIRAN
        listKehadiran.clear()
    }

    private fun readKaryawanDb(){
        // BACA DATA KARYAWAN UNTUK DITAMPILKAN KE KEHADIRAN
        dbKaryawanHelper.setReference(Karyawan.KARYAWAN_DB_REFERENCE).addOnceListValuesEventListenerCallback(object :
            IDatabaseHelper.ListValuesEventListenerCallback<Karyawan> {
            override fun onDataUpdate(values: ArrayList<Karyawan>?, ids: ArrayList<String>?) {
                if (values != null && ids != null) {
                    if (values.size != 0 && ids.size != 0) {
                        listIdKaryawan.clear()
                        listIdKaryawan.addAll(ids)
                        adapterKehadiran.setData(values)
                    }
                }
            }

            override fun onListValueEventListenerCancelled(errorMessage: String?) {
                Log.e(TAG, "onListValueEventListenerCancelled:$errorMessage")
            }
        }, Karyawan::class.java)
    }
}