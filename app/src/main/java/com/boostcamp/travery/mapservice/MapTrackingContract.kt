package com.boostcamp.travery.mapservice

import android.location.Location
import com.boostcamp.travery.data.model.Route
import com.google.android.gms.maps.model.LatLng

interface MapTrackingContract {
    interface Model {
        //경과 시간을 받아오는 함수
        fun getTotalSecond(): Int

        //종료했을 때 보낼 함수
        fun getFinishData(): Route
        //현재 위치 받아오는 함수
        fun getLastLocation(): Location?
        //주행 중일때 지금까지 저장된 경로 리스트를 보내주는 함수
        fun getLocationList(): ArrayList<LatLng>
    }
}