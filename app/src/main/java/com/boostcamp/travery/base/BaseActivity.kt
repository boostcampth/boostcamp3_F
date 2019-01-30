package com.boostcamp.travery.base

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.boostcamp.travery.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.progressbar_loading.*


open class BaseActivity : AppCompatActivity() {
    var progressDialog: AppCompatDialog? = null

    // usage : "토스트".toast
    fun Any.toast(duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(baseContext, this.toString(), duration).show()
    }

    fun progressOn(message: String?) {
        if (this.isFinishing) {
            return
        }
        if (progressDialog?.isShowing == true) {
            progressSet(message)
        } else {
            progressDialog = AppCompatDialog(this)
                    .apply {
                        setCancelable(false)
                        setContentView(R.layout.progressbar_loading)
                        show()
                    }
        }

        progressDialog?.let {
            it.findViewById<TextView>(R.id.tv_pregress_message)?.text = message
            val imageView = it.findViewById<ImageView>(R.id.iv_frame_loading) ?: return
            Glide.with(this)
                    .load(R.drawable.progress_bar)
                    .into(imageView)
        }
    }

    private fun progressSet(message: String?) {
        if (progressDialog?.isShowing != false) {
            return
        }
        tv_pregress_message.text = message ?: " "
    }

    fun progressOff() {
        if (progressDialog?.isShowing != false) {
            progressDialog?.dismiss()
        }
    }
}