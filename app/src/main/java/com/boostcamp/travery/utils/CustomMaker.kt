package com.boostcamp.travery.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.boostcamp.travery.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_marker_layout.view.*

object CustomMaker {

    fun create(context: Context, path: String): Bitmap {
        val marker = LayoutInflater.from(context).inflate(R.layout.custom_marker_layout, null)

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(marker.measuredWidth, marker.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        marker.draw(canvas)

        return bitmap
    }
}