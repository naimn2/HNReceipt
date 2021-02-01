package com.kakzain.hnreceipt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakzain.hnreceipt.db.lokal.MyConstants
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.adapter.ListKehadiranAdapter
import com.kakzain.hnreceipt.adapter.ListPanenLahanAdapter
import com.kakzain.hnreceipt.adapter.ListSopirAdapter
import com.kakzain.hnreceipt.db.IDatabaseHelper
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.helper.CurrencyFormatter
import com.kakzain.hnreceipt.helper.DateFormatter
import com.kakzain.hnreceipt.helper.UnitValidator
import com.kakzain.hnreceipt.model.*
import kotlinx.android.synthetic.main.activity_create_d_o.*
import kotlinx.android.synthetic.main.dialog_form_input_d_o_more_info_detail.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class CreateDOActivity : AppCompatActivity() {
    companion object { const val ID_DO_EXTRA = "idDOExtra" }

    private val TAG = CreateDOActivity::class.java.simpleName
    private lateinit var dbKaryawanHelper: IDatabaseHelper<Karyawan>
    private lateinit var dbDOHelper: IDatabaseHelper<DeliveryOrder>
    private lateinit var dbPanenSawitLahan: IDatabaseHelper<PanenSawitLahan>
    private lateinit var adapterKehadiran: ListKehadiranAdapter
    private lateinit var panenLahanAdapter: ListPanenLahanAdapter
    private lateinit var listKaryawan: ArrayList<Karyawan>
    private lateinit var listIdKaryawan: ArrayList<String>
    private lateinit var mapKehadiran: HashMap<Int, Kehadiran>
    private lateinit var currentDO: DeliveryOrder
    private var idDO: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_d_o)

        setSupportActionBar(toolbar_activityCreateDO)
        title = getString(R.string.create_do)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val karyawanMap = MyConstants.getAllKaryawan(this)
        listKaryawan = ArrayList(karyawanMap.values)
        Log.d(TAG, "onCreate: listKaryawan size: ${listKaryawan.size}")
        listIdKaryawan = ArrayList(karyawanMap.keys)
        idDO = intent.getStringExtra(ID_DO_EXTRA)

        currentDO = DeliveryOrder()
        dbKaryawanHelper = FirestoreHelper()
        dbDOHelper = FirestoreHelper()
        dbDOHelper.setReference(DeliveryOrder.DO_DB_REFERENCE+"/"+idDO)
        dbPanenSawitLahan = FirestoreHelper()
        mapKehadiran = HashMap()

        btn_activityCreateDO_tambahLahanPanen.setOnClickListener {
            showDialogTambahLahanPanen()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        panenLahanAdapter = ListPanenLahanAdapter(this)
        rv_activityCreateDO_listLahanPanen.adapter = panenLahanAdapter
        rv_activityCreateDO_listLahanPanen.layoutManager = linearLayoutManager
        readDOdb()
    }

    private fun initListenerPanenLahanAdapter() {
        panenLahanAdapter.setOnMenuClickListenerCallback { _, position ->
            if (currentDO.panenSawitLahan != null) {
                showDialogRemoveItemPanenLahanConfirmation(position)
            }
        }
    }

    private fun readDOdb(){
        // BACA DATA DO SEBELUM UPDATE VIEW
        showLoadingPanen(true)
        dbDOHelper.addValueEventListenerCallback(
            object : IDatabaseHelper.ValueEventListenerCallback<DeliveryOrder> {
                override fun onDataUpdate(value: DeliveryOrder?, id: String?) {
                    showLoadingPanen(false)
                    if (value != null){
                        currentDO = value

                        // UPDATE VIEW
                        tv_activityCreateDO_beratTotal.text = String.format("%.01f KG", value.beratTotal)
                        tv_activityCreateDO_tanggal.text = DateFormatter.getDate(value.tanggal.toDate().time)

                        // SHOW BTN ADD LAHAN PANEN
                        btn_activityCreateDO_tambahLahanPanen.visibility = View.VISIBLE

                        // DISABLE EDIT / ADD LAHAN PANEN IF COMMITTED
                        // ADD PANEN LAHAN MORE ITEM LISTENER WHEN _DO IS NOT COMMITTED
                        if (currentDO.isCommitted){
                            btn_activityCreateDO_tambahLahanPanen.isEnabled = false
                        } else {
                            btn_activityCreateDO_tambahLahanPanen.isEnabled = true
                            initListenerPanenLahanAdapter()
                        }

                        if (currentDO.panenSawitLahan != null && currentDO.panenSawitLahan.size > 0) {
                            panenLahanAdapter.setData(currentDO.panenSawitLahan)    // UPDATE LIST PANEN LAHAN ADAPTER
                            tv_activityCreateDO_noLahanPanen.visibility = View.INVISIBLE    // HIDE NO LAHAN PANEN
                        } else {
                            tv_activityCreateDO_noLahanPanen.visibility = View.VISIBLE    // SHOW NO LAHAN PANEN
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
        val etBeratBersih = dialogView.findViewById<EditText>(R.id.et_dialogTambahLahanPanen_beratBersih)
        val etBeratBrondol = dialogView.findViewById<EditText>(R.id.et_dialogTambahLahanPanen_beratBrondol)
        val rvKehadiran = dialogView.findViewById<RecyclerView>(R.id.rv_dialogTambahLahanPanen_kehadiranKaryawan)
        val tvTutup = dialogView.findViewById<TextView>(R.id.tv_dialogTambahLahanPanen_tutup)

        // SETUP SPINNER LAHAN
        val mapLahan = MyConstants.getNamaLahanAndIdMap(this, getString(R.string.pilih_lahan))
        val namaLahanListSpin = ArrayList<String>(mapLahan.values)
        val idLahanList = ArrayList<Int>(mapLahan.keys)
        val spinArrayAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, namaLahanListSpin)
        spinLahan.adapter = spinArrayAdapter

        // SIAPKAN RECYCLER VIEW KEHADIRAN KARYAWAN
        val linearLayoutManager = LinearLayoutManager(this)
        adapterKehadiran = ListKehadiranAdapter(this)
        adapterKehadiran.setData(listKaryawan)

        // ON POSISI SPINNER LISTENER CALLBACK
        adapterKehadiran.setOnHadirListenerCallback { i, position ->
            val idKaryawan = listIdKaryawan[position]
            val posisiKeys = ArrayList<Int>(MyConstants.getAllPosisi(this).keys)
            Log.d(TAG, "showDialogTambahLahanPanen: i: $i")
            Log.d(TAG, "showDialogTambahLahanPanen: position: $position")
            if (i == 0){
                mapKehadiran.remove(position)
            } else {
                mapKehadiran[position] = Kehadiran(idKaryawan, posisiKeys[i-1])
            }
        }
        rvKehadiran.setHasFixedSize(true)

        rvKehadiran.layoutManager = linearLayoutManager
        rvKehadiran.adapter = adapterKehadiran
        Log.d(TAG, "adapterKehadiranCount: ${rvKehadiran.adapter?.itemCount}")

        // BUILD ALERT DIALOG TAMBAH LAHAN PANEN
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()

        tvTutup.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener {
            val indeksLahan = spinLahan.selectedItemPosition
            Log.d(TAG, "showDialogTambahLahanPanen: indeksLahan $indeksLahan")

            // CEK APAKAH BELUM PILIH LAHAN
            if (indeksLahan == 0){
                spinLahan.requestFocus()
                Toast.makeText(this, "Pilih Lahan Sawit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idSelectedLahan = idLahanList[indeksLahan]

            try {
                // SIAPKAN MODEL PANEN LAHAN SAWIT UNTUK DITULIS KE DB
                val beratBersih = etBeratBersih.text.toString().toFloat()
                val beratBrondol = etBeratBrondol.text.toString().toFloat()
                val kehadiran = ArrayList(mapKehadiran.values)
                updateLahanPanenDOInDB(PanenSawitLahan(idSelectedLahan, kehadiran, beratBersih, beratBrondol))
                alertDialog.dismiss()
            }
            catch (numberFormatException: NumberFormatException){
                Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
            }
            catch (exc: Exception){
                Toast.makeText(this, "Error tidak dikenal", Toast.LENGTH_SHORT).show()
            }
        }
        // SHOw ALERT DIALOG
        alertDialog.show()
    }

    private fun updateLahanPanenDOInDB(lahanPanen: PanenSawitLahan){
        if (currentDO.panenSawitLahan == null) {
            currentDO.panenSawitLahan = ArrayList()
        }
        currentDO.panenSawitLahan.add(lahanPanen)
        // PERBARUI BERAT TOTAL DO
        currentDO.beratTotal += lahanPanen.beratBersih + lahanPanen.beratBrondol
        // TULIS DO TERBARU KE DB
        dbDOHelper.writeValue(currentDO)
        // CLEAR LIST KEHADIRAN
        mapKehadiran.clear()
    }

    private fun showDialogFormInputDOmoreInformationDetail(){
        // INIT VIEW
        val mView = layoutInflater.inflate(R.layout.dialog_form_input_d_o_more_info_detail,
                null, false)
        val etRefaksi = mView.findViewById<EditText>(R.id.et_dialogDOMoreInfo_refaksi)
        val etHargaSawit = mView.findViewById<EditText>(R.id.et_dialogDOMoreInfo_hargaSawit)
        val etUpahKaryawan = mView.findViewById<EditText>(R.id.et_dialogDOMoreInfo_upah)
        val etUpahSopir = mView.findViewById<EditText>(R.id.et_dialogDOMoreInfo_upahSopir)
        val etUpahBrondol = mView.findViewById<EditText>(R.id.et_dialogDOMoreInfo_upahBrondol)
        val rvListSopir = mView.findViewById<RecyclerView>(R.id.rv_dialogDOMoreInfo_listKaryawanSopir)

        etRefaksi.setText(UnitValidator.validateUnitPercent((currentDO.refaksi*100).roundToInt()), TextView.BufferType.EDITABLE)
        etHargaSawit.setText(UnitValidator.validateUnitCurrency(currentDO.hargaSawit), TextView.BufferType.EDITABLE)
        if (currentDO.upah != null) {
            etUpahKaryawan.setText(
                UnitValidator.validateUnitCurrency(currentDO.upah[MyConstants.POSISI_PEMANEN_INDEKS].upah.toInt()),
                TextView.BufferType.EDITABLE
            )
            etUpahSopir.setText(
                UnitValidator.validateUnitPercent((currentDO.upah[MyConstants.POSISI_SOPIR_INDEKS].upah*100).roundToInt()),
                TextView.BufferType.EDITABLE
            )
            etUpahBrondol.setText(
                UnitValidator.validateUnitCurrency(currentDO.upah[MyConstants.POSISI_BRONDOL_INDEKS].upah.toInt()),
                TextView.BufferType.EDITABLE
            )
        }

        // INIT FOCUS CHANGE LISTENER FOR VALIDATION VALUE
        initFocusChangeListenerForValidateValue(etRefaksi, etHargaSawit, etUpahKaryawan,
            etUpahBrondol, etUpahSopir)

        // SETUP RECYCLER VIEW LIST SOPIR
        rvListSopir.layoutManager = LinearLayoutManager(this)
        val listSopirAdapter = ListSopirAdapter(this, currentDO.sopir)
        rvListSopir.adapter = listSopirAdapter
        listSopirAdapter.setData(listKaryawan, listIdKaryawan)
        val mapSopir = HashMap<Int, Kehadiran>()
        // SET ON SOPIR CHECK CHANGE LISTENER
        listSopirAdapter.setOnSopirListenerCallback { b, i ->
            if (b){
                mapSopir[i] = Kehadiran(listIdKaryawan[i],
                    MyConstants.getNamaPosisiIndeks(this)["Sopir"]!!)
                Log.d(TAG, "map sopir size: ${mapSopir.size}")
            } else {
                mapSopir.remove(i)
            }
        }
        // BUILD ALERT DIALOG DO MORE DETAILS
        val alertDialog = AlertDialog.Builder(this)
                .setView(mView).create()

        // ADD ON CETAK DO BUTTON CLICK LISTENER
        mView.findViewById<Button>(R.id.btn_dialogDOMoreInfo_saveDO).setOnClickListener {
            try {
                val refaksi = BigDecimal(UnitValidator.unitPercentToInt(
                    etRefaksi.text.toString())).divide(BigDecimal("100"))
                val hargaSawit = UnitValidator.unitCurrencytoInt(etHargaSawit.text.toString())
                // PERHATIKAN URUTAN UPAH
                val upahPemanen = Upah(MyConstants.POSISI_PEMANEN_INDEKS, UnitValidator.unitCurrencytoInt(
                    etUpahKaryawan.text.toString()).toFloat())
                val upahPengangkut =  Upah(MyConstants.POSISI_PENGANGKUT_INDEKS, UnitValidator.unitCurrencytoInt(
                    etUpahKaryawan.text.toString()).toFloat())
                val upahSopir = Upah(MyConstants.POSISI_SOPIR_INDEKS, (BigDecimal(UnitValidator.unitPercentToInt(
                    etUpahSopir.text.toString())).divide(BigDecimal("100"))).toFloat())
                val upahBrondol = Upah(MyConstants.POSISI_BRONDOL_INDEKS, UnitValidator.unitCurrencytoInt(
                    etUpahBrondol.text.toString()).toFloat())
                val upah = ArrayList<Upah>()
                upah.add(upahPemanen)
                upah.add(upahPengangkut)
                upah.add(upahSopir)
                upah.add(upahBrondol)
                updateCurrentDOMoreDetail(
                        refaksi.toFloat(),
                        hargaSawit,
                        upah,
                        ArrayList<Kehadiran>(mapSopir.values)
                )
                alertDialog.dismiss()
            } catch (err: NumberFormatException){
                Log.e(TAG, "showDialogFormInputDOmoreInformationDetail: ${err.message}", err)
                Toast.makeText(this, "Data Tidak Valid", Toast.LENGTH_SHORT).show()
            }
        }
        // ADD ON BTN BATAL CLICK LISTENER
        mView.findViewById<TextView>(R.id.tv_dialogDOMoreInfo_batal).setOnClickListener {
            alertDialog.dismiss()
        }
        // SHOW ALERT DIALOG DO MORE DETAILS
        alertDialog.show()
    }

    private fun updateCurrentDOMoreDetail(refaksi: Float, hargaSawit: Int,
                                                upah: List<Upah>, sopir: List<Kehadiran>) {
        // UPDATE DO IN DB
        currentDO.refaksi = refaksi
        currentDO.hargaSawit = hargaSawit
        currentDO.upah = upah
        currentDO.sopir = sopir
        dbDOHelper.writeValue(currentDO)
    }

    private fun showDialogCommitDOconfirmation() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.cetak_do))
                .setMessage(getString(R.string.cetak_do_message_confirmation))
                .setIcon(R.drawable.ic_task_complete)
                .setPositiveButton(R.string.ya_caps) { _, _ ->
                    // UPDATE DO STATUS TO 'COMMITTED'
                    if (!currentDO.hasComplete()){
                        showDialogUnCompleteDO()
                        return@setPositiveButton
                    }
                    currentDO.isCommitted = true
                    dbDOHelper.writeValue(currentDO)
                    setEditableViewDO(false)
                    Toast.makeText(applicationContext, "Delivery Order Dicetak", Toast.LENGTH_SHORT).show()

                    // CREATE SUMMARY
                    summaryDO(currentDO)
                }
                .setNegativeButton(R.string.batal_caps) { _, _ ->
                    // nothing
                }
                .show()
    }

    private fun summaryDO(mDO: DeliveryOrder){
        val listPenggajian = ArrayList<Penggajian>()
        val listPanenLahan = mDO.panenSawitLahan ?: return
        val mapKaryawan = MyConstants.getAllKaryawan(this)

        // HITUNG HARGA OMZET
        val beratBersihDO = mDO.beratTotal - (mDO.beratTotal*(mDO.refaksi*100).roundToLong()/100)
        val hargaOmzet = beratBersihDO*mDO.hargaSawit

        // <=========================> PERHITUNGAN GAJI SETIAP KARYAWAN <=========================>
        var gajiTotalKaryawan = 0.0
        // TELUSURI SETIAP KARYAWAN
        for (keyKaryawan in mapKaryawan.keys){
            // INIT GAJI AWAL
            var gaji = 0.0

            // TELUSURI SETIAP LAHAN PANEN
            for (currentPanenLahan in listPanenLahan){

                val beratBersihPanen = currentPanenLahan.beratBersih
                val jumlahHadir = currentPanenLahan.kehadiran.size

                // TELUSURI SETIAP KEHADIRAN
                kehadiran@
                for (currentKehadiran in currentPanenLahan.kehadiran){

                    // CEK KEHADIRAN CURRENT KARYAWAN LALU UPDATE GAJI
                    // KHUSUS PEMANEN/PENGANGKUT
                    val mapNamaPosisiIndeks = MyConstants.getNamaPosisiIndeks(this)
                    if (keyKaryawan == currentKehadiran.idKaryawan){
                        val upahPMNPK = mDO.upah[currentKehadiran.posisi].upah
                        gaji += beratBersihPanen*upahPMNPK/jumlahHadir
                        break@kehadiran
                    }
                }
            }
            // CEK SOPIR LALU UPDATE GAJI
            // KHUSUS SOPIR
            sopir@
            for (currentSopir in mDO.sopir){
                if (currentSopir.idKaryawan == keyKaryawan){
                    val upahSopir = mDO.upah[currentSopir.posisi].upah
                    gaji += upahSopir*hargaOmzet
                    break@sopir
                }
            }
            val penggajian = Penggajian(keyKaryawan, gaji)
            listPenggajian.add(penggajian)
            gajiTotalKaryawan += gaji
            Log.d(TAG,"gaji(${mapKaryawan[keyKaryawan]?.nama}) = ${CurrencyFormatter.format(gaji)}")
        }
        Log.d(TAG, "totalGaji: $gajiTotalKaryawan")

        val hargaBersih = hargaOmzet - gajiTotalKaryawan
        val summary = Summary(listPenggajian, hargaBersih.toFloat())

        // TULIS SUMMARY KE DB
        FirestoreHelper<Summary>().setReference(Summary.SUMMARY_DB_REFERENCE+"/"+idDO).writeValue(summary)
        updateUItoSummary()
    }

    private fun showDialogRemoveItemPanenLahanConfirmation(position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.hapus_panen_lahan)
            .setMessage(R.string.hapus_item_message_confirmation)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.ya_caps) { _, _ ->
                removeItemPanenLahan(position)
            }
            .setNegativeButton(R.string.batal_caps){ _, _ ->
                // do nothing
            }
            .show()
    }

    private fun removeItemPanenLahan(position: Int) {
        currentDO.panenSawitLahan.removeAt(position)
        panenLahanAdapter.setData(currentDO.panenSawitLahan)
        dbDOHelper.writeValue(currentDO)
    }

    private fun showDialogUnCompleteDO() {
        AlertDialog.Builder(this)
            .setTitle(R.string.uncomplete_do)
            .setMessage(R.string.uncomplete_do_alert_message)
            .setIcon(R.drawable.ic_baseline_close_red)
            .setPositiveButton(R.string.tutup) { _, _ ->
                // do nothing
            }
            .show()
    }

    private fun initFocusChangeListenerForValidateValue(
        etRefaksi: EditText, etHargaSawit: EditText, etUpahKaryawan: EditText,
        etUpahBrondol: EditText, etUpahSopir: EditText
    ){
        etRefaksi.setOnFocusChangeListener { _, b ->
            val it = etRefaksi.text
            if (!b && !TextUtils.isEmpty(it)){
                val currentValue = UnitValidator.unitPercentToInt(it?.toString())
                Log.d(TAG, "toInt: ${currentValue}")
                etRefaksi.setText(
                    UnitValidator.validateUnitPercent(currentValue),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        etHargaSawit.setOnFocusChangeListener { _, b ->
            val it = etHargaSawit.text
            if (!b && !TextUtils.isEmpty(it)){
                val currentValue = UnitValidator.unitCurrencytoInt(it?.toString())
                Log.d(TAG, "toInt: ${currentValue}")
                etHargaSawit.setText(
                    UnitValidator.validateUnitCurrency(currentValue),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        etUpahKaryawan.setOnFocusChangeListener { _, b ->
            val it = etUpahKaryawan.text
            if (!b && !TextUtils.isEmpty(it)){
                val currentValue = UnitValidator.unitCurrencytoInt(it?.toString())
                Log.d(TAG, "toInt: ${currentValue}")
                etUpahKaryawan.setText(
                    UnitValidator.validateUnitCurrency(currentValue),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        etUpahSopir.setOnFocusChangeListener { _, b ->
            val it = etUpahSopir.text
            if (!b && !TextUtils.isEmpty(it)){
                val currentValue = UnitValidator.unitPercentToInt(it?.toString())
                Log.d(TAG, "toInt: ${currentValue}")
                etUpahSopir.setText(
                    UnitValidator.validateUnitPercent(currentValue),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        etUpahBrondol.setOnFocusChangeListener { _, b ->
            val it = etUpahBrondol.text
            if (!b && !TextUtils.isEmpty(it)){
                val currentValue = UnitValidator.unitCurrencytoInt(it?.toString())
                Log.d(TAG, "toInt: ${currentValue}")
                etUpahBrondol.setText(
                    UnitValidator.validateUnitCurrency(currentValue),
                    TextView.BufferType.EDITABLE
                )
            }
        }
    }

    private fun setEditableViewDO(b: Boolean) {
        btn_activityCreateDO_tambahLahanPanen.isEnabled = b
        if (!b) {
            panenLahanAdapter.setOnMenuClickListenerCallback(null)
        } else {
            initListenerPanenLahanAdapter()
        }
    }

    private fun showLoadingPanen(b: Boolean){
        if (b){
            pb_activityCreateDO_loadingPanenLahan.visibility = View.VISIBLE
            rv_activityCreateDO_listLahanPanen.visibility = View.INVISIBLE
        } else {
            pb_activityCreateDO_loadingPanenLahan.visibility = View.GONE
            rv_activityCreateDO_listLahanPanen.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_create_d_o, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_menuToolbarCreateDO_moreDetailDO){
            if (!currentDO.isCommitted) {
                showDialogFormInputDOmoreInformationDetail()
            } else {
                Toast.makeText(
                    this,
                    "Tidak dapat mengubah rincian DO yang sudah dicetak",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (item.itemId == R.id.item_menuToolbarCreateDO_commitDO){
            if (!currentDO.isCommitted){
                showDialogCommitDOconfirmation()
            } else {
                updateUItoSummary()
            }
        } else {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUItoSummary() {
        val mIntent = Intent(this, SummaryActivity::class.java)
        mIntent.putExtra(SummaryActivity.ID_D_O_EXTRA, idDO)
        startActivity(mIntent)
    }
}
