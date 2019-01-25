package com.boostcamp.travery

interface MapTrackingContract {
    interface Model {
        //경과 시간을 받아오는 함수
        fun getTotalSecond(): Int
        fun getFinishData(): Int
    }
}