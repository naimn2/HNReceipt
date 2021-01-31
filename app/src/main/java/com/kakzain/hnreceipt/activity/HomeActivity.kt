package com.kakzain.hnreceipt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.model.DeliveryOrder
import kotlinx.android.synthetic.main.activity_daftar_karyawan.*
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun createDOHandler(view: View) {
        val mDO = DeliveryOrder(null, 0, 0.0f, 0.0f, Date(), null, null)
        val id: String = FirestoreHelper<DeliveryOrder>().setReference(DeliveryOrder.DO_DB_REFERENCE).pushWriteValue(mDO)
        Toast.makeText(this, "DO baru telah dibuat", Toast.LENGTH_SHORT).show()
        val mIntent = Intent(this, CreateDOActivity::class.java)
        mIntent.putExtra(CreateDOActivity.ID_DO_EXTRA, id)
        startActivity(mIntent)
    }

    fun daftarDOHandler(view: View) {
        val mIntent = Intent(this, DaftarDOActivity::class.java)
        startActivity(mIntent)
    }

    fun daftarKaryawanHandler(view: View) {
        val mIntent = Intent(this, DaftarKaryawanActivity::class.java)
        startActivity(mIntent)
    }

    fun daftarLahanHandler(view: View) {

    }

    private var isClickBackTwice = false;

    override fun onBackPressed() {
        if (isClickBackTwice) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press Back Twice to Close", Toast.LENGTH_SHORT).show()
            isClickBackTwice = true
            Handler()
                .postDelayed(Runnable {
                    isClickBackTwice = false
                }, 2000)
        }
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().signOut()
        super.onDestroy()
    }
}