package com.kakzain.hnreceipt.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kakzain.hnreceipt.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun btnLoginHandler(view: View) {
        val email = et_activityLogin_email.text.toString()
        val password = et_activityLogin_password.text.toString()

        if (TextUtils.isEmpty(email)){
            et_activityLogin_email.requestFocus()
            et_activityLogin_email.error = getString(R.string.tidak_boleh_kosong)
            return
        }
        if (!isEmailValid(email)){
            et_activityLogin_email.requestFocus()
            et_activityLogin_email.error = getString(R.string.unvalid_email)
            return
        }

        if (TextUtils.isEmpty(password)){
            et_activityLogin_password.requestFocus()
            et_activityLogin_password.error = getString(R.string.tidak_boleh_kosong)
            return
        }

        val mDialogProgress = ProgressDialog(this)
        mDialogProgress.setMessage(getString(R.string.mohon_tunggu))
        mDialogProgress.show()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                mDialogProgress.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.login_berhasil),
                        Toast.LENGTH_SHORT
                    ).show()
                    val mIntent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(mIntent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.wrong_email_or_password),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "onComplete: Failed ${task.exception}")
                }
            }
    }
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }
}