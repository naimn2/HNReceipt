package com.kakzain.hnreceipt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.kakzain.hnreceipt.R
import com.kakzain.hnreceipt.db.firebase.FirestoreHelper
import com.kakzain.hnreceipt.model.DeliveryOrder
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun createDOHandler(view: View) {
        val mDO = DeliveryOrder(null, 0.0f, Timestamp(Date()), 0, 180, 0.0f)
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

    }

    fun daftarLahanHandler(view: View) {

    }
}