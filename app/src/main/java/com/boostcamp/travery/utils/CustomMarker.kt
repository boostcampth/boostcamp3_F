package com.boostcamp.travery.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.custom_marker_layout.view.*
import java.io.File

object CustomMarker {

    fun create(context: Context, path: String?): Bitmap {
        val marker = LayoutInflater.from(context).inflate(R.layout.custom_marker_layout, null)

        if(path.isNullOrEmpty()){
            marker.cv_action_image.setImageResource(R.drawable.empty_action_image)
        }else{
            val image=BitmapFactory.decodeFile(File(path).absolutePath)
            marker.cv_action_image.setImageBitmap(image)
        }

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