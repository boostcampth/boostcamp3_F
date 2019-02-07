package com.boostcamp.travery.data.model

import com.google.android.gms.maps.model.LatLng

data class Suggestion(
    var location: LatLng,
    var startTime: Long = 0,
    var endTime: Long = 0
)