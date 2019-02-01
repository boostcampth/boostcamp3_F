package com.boostcamp.travery.base

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.boostcamp.travery.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.progressbar_loading.*

abstract class BaseActivity<T: ViewDataBinding> : AppCompatActivity() {
    private var progressDialog: AppCompatDialog? = null

    abstract val layoutResourceId: Int

    lateinit var viewDataBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutResourceId, null, false)
    }

    fun onProgress(message: String?) {
        if (this.isFinishing) {
            return
        }
        if (progressDialog?.isShowing == true) {
            setProgress(message)
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

    private fun setProgress(message: String?) {
        if (progressDialog?.isShowing != false) {
            return
        }
        tv_pregress_message.text = message ?: " "
    }

    fun offProgress() {
        if (progressDialog?.isShowing != false) {
            progressDialog?.dismiss()
        }
    }
}