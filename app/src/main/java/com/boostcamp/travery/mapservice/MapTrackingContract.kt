package com.boostcamp.travery.mapservice

import android.location.Location
import com.boostcamp.travery.data.model.Route

interface MapTrackingContract {
    interface Model {
        //경과 시간을 받아오는 함수
        fun getTotalSecond(): Int
        fun getFinishData(): Route
        fun getLastLocation(): Location?
    }
}