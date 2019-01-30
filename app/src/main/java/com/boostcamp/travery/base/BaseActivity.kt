package com.boostcamp.travery.base

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.boostcamp.travery.R
import kotlinx.android.synthetic.main.progressbar_loading.*


open class BaseActivity : AppCompatActivity() {

    companion object {
        var progressDialog: AppCompatDialog? = null
    }

    // usage : "토스트".toast
    fun Any.toast(duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(baseContext, this.toString(), duration).show()
    }

    fun progressON(activity: Activity?, message: String?) {
        if (activity?.isFinishing != false) {
            return
        }
        if (progressDialog?.isShowing == true) {
            progressSET(message)
        } else {
            progressDialog = AppCompatDialog(activity)
                .apply {
                setCancelable(false)
                setContentView(R.layout.progressbar_loading)
                show()
            }
        }
    }

    private fun progressSET(message: String?) {
        if (progressDialog?.isShowing != false) {
            return
        }
        tv_pregress_message.text = message ?: " "
    }

    fun progressOFF() {
        if (progressDialog?.isShowing != false) {
            progressDialog?.dismiss()
        }
    }
}