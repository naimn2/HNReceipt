package com.kakzain.hnreceipt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        btn_activityHome_buatDO.setOnClickListener {
            val mDO = DeliveryOrder(null, 0.0f, Timestamp(Date()), 0, 180, 0.0f)
            val id: String = FirestoreHelper<DeliveryOrder>().setReference(DeliveryOrder.DO_DB_REFERENCE).pushWriteValue(mDO)

            val mIntent = Intent(this, CreateDOActivity::class.java)
            mIntent.putExtra(CreateDOActivity.ID_DO_EXTRA, id)
            startActivity(mIntent)
        }
    }
}