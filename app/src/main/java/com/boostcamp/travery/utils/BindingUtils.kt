package com.boostcamp.travery.utils

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import android.os.Build
import androidx.annotation.RequiresApi
import android.widget.TextView
import com.boostcamp.travery.R
import com.boostcamp.travery.custom.CircleImageView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

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
            circleImageView.setImageDrawable(circleImageView.context.getDrawable(R.mipmap.ic_launcher_round))
        }
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("android:date")
    fun setDate(textView: TextView, date: Date) {
        val dayTime = SimpleDateFormat("yyyy.MM.dd")
        textView.text = dayTime.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("bind:startTime","bind:endTime")
    fun setTime(textView: TextView, startTime: Long, endTime: Long) {
        val start = SimpleDateFormat("yyyy.MM.dd - kk:mm")
        val end = SimpleDateFormat("kk:mm")
        textView.text = "${start.format(Date(startTime))} ~ ${end.format(Date(endTime))}"
    }
}