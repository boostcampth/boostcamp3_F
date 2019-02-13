package com.boostcamp.travery.mapservice

import android.util.Log
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.data.model.TimeCode
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MapTrackingRepository {
    private val timeCodeList = ArrayList<TimeCode>()
    private val timeCode : PublishSubject<TimeCode> = PublishSubject.create()

    private val suggestList = ArrayList<Suggestion>()
    private val suggest : PublishSubject<Suggestion> = PublishSubject.create()

    private val second : PublishSubject<Int> = PublishSubject.create()

    private val userActionLocateList = ArrayList<LatLng>()

    private var startTime = 0L
    private var totalDistance = 0L

    companion object {
        @Volatile
        private var INSTANCE: MapTrackingRepository? = null

        fun getInstance() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MapTrackingRepository().also { INSTANCE = it }
        }
    }

    fun addTotalDistance(distance: Float){
        totalDistance += distance.toLong()
    }

    fun getTotalDistance(): Long{
        return totalDistance
    }

    fun addTimeCode(timeCode: TimeCode) {
        if(startTime != 0L)
            timeCodeList.add(timeCode)
        this.timeCode.onNext(timeCode)
        //Log.d("lolocation",timeCode.toString())
    }

    fun setSecond(second: Int){
        this.second.onNext(second)
    }

    fun getSecond(): Observable<Int> {
        return second
    }

   fun getTimeCode(): Observable<TimeCode> {
        return timeCode
    }

    fun getTimeCodeList(): ArrayList<TimeCode> {
        return timeCodeList
    }

    fun getStartTime(): Long {
        return startTime
    }

    fun setStartTime(startTime: Long) {
        this.startTime = startTime
    }

    fun getSuggest(): Observable<Suggestion> {
        return suggest
    }

    fun getSuggestList(): ArrayList<Suggestion> {
        return suggestList
    }

    fun addSuggest(suggest: Suggestion) {
        if(startTime != 0L)
            suggestList.add(suggest)
        this.suggest.onNext(suggest)
    }

    fun removeSuggestItem(position: Int) {
        suggestList.removeAt(position)
    }

    fun clearData() {
        timeCodeList.clear()
        suggestList.clear()
        startTime = 0L
        totalDistance = 0L
    }

    fun addUserActionLocate(locate: LatLng){
        userActionLocateList.add(locate)
    }

    fun getUsetActionLocateList(): ArrayList<LatLng>{
        return userActionLocateList
    }
}