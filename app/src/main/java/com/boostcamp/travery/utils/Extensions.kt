package com.boostcamp.travery.utils

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng


/**
 * DpToPx
 */
fun Number.toPx(): Int =(this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

// usage : "토스트".toast(context)
fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this.toString(), duration).show()
}

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}
