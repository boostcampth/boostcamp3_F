package com.boostcamp.travery.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeCode(
        var coordinate:LatLng,
        var timeStamp: Long = 0
):Parcelable