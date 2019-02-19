package com.boostcamp.travery.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.progressbar_loading.*


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    private var progressDialog: AppCompatDialog? = null

    abstract val layoutResourceId: Int

    lateinit var viewDataBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutResourceId, null, false)
    }

    protected fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    fun onProgress(message: String = resources.getString(R.string.progress_bar_message)) {
        if (this.isFinishing) {
            return
        }
        if (progressDialog?.isShowing == true) {
            setProgress(message)
        } else {
            progressDialog = AppCompatDialog(this)
                    .apply {
                        setCancelable(false)
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        setContentView(R.layout.loading_dialog)
                        show()
                    }
        }

        progressDialog?.let {
            it.findViewById<TextView>(R.id.tv_pregress_message)?.text = message
            val imageView = it.findViewById<ImageView>(R.id.iv_frame_loading) ?: return
            GlideApp.with(this)
                .asGif()
                .load(R.drawable.progress_bar)
                .placeholder(R.drawable.progress_bar)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}