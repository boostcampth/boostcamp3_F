package com.boostcamp.travery.main

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.TextView
import com.boostcamp.travery.R
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

object BindingUtils {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @JvmStatic
    @BindingAdapter("android:mapImage")
    fun setMapImage(circleImageView: CircleImageView, path: String) {
        if (!path.isEmpty()) {
            Glide.with(circleImageView.context)
                .load(path)
                .into(circleImageView)
        } else {
            circleImageView.setImageDrawable(circleImageView.context.getDrawable(R.mipmap.ic_launcher))
        }
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("android:date")
    fun setDate(textView: TextView, date: Long) {
        val dayTime = SimpleDateFormat("yyyy.MM.dd")
        textView.text = dayTime.format(System.currentTimeMillis())
    }
}