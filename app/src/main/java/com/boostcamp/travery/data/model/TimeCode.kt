package com.boostcamp.travery.data.model

import com.google.android.gms.maps.model.LatLng

data class TimeCode(
        var coordinate:LatLng,
        var timeStamp: Long = 0
)