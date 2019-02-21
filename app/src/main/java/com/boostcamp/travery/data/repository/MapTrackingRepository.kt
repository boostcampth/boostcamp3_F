package com.boostcamp.travery.data.repository

import android.location.Location
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.eventbus.EventBus
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MapTrackingRepository {
    private val timeCodeList = ArrayList<TimeCode>()
    private val timeCode: PublishSubject<Location> = PublishSubject.create()

    private val suggestList = ArrayList<Suggestion>()
    private val suggest: PublishSubject<Int> = PublishSubject.create()
    private var suggestListSize = 0

    private val second: PublishSubject<Int> = PublishSubject.create()
    private val distance: PublishSubject<Long> = PublishSubject.create()

    private val userActionList = HashMap<Long, UserAction>()

    private var startTime = 0L
    private var totalDistance = 0L

    companion object {
        @Volatile
        private var INSTANCE: MapTrackingRepository? = null

        fun getInstance() = INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                            ?: MapTrackingRepository().also { INSTANCE = it }
                }
    }

    fun addTotalDistance(distance: Float) {
        if (startTime != 0L)
            totalDistance += distance.toLong()
        //this.distance.onNext(totalDistance)
    }

    fun getDistance(): Observable<Long> {
        return distance
    }

    fun getTotalDistance(): Long {
        return totalDistance
    }

    fun addTimeCode(location: Location) {
        val timeCode = TimeCode(LatLng(location.latitude, location.longitude), location.time)
        if (startTime != 0L)
            timeCodeList.add(timeCode)

        this.timeCode.onNext(location)
    }

    fun setSecond(second: Int) {
        this.second.onNext(second)
    }

    fun getSecond(): Observable<Int> {
        return second
    }

    fun getTimeCode(): Observable<Location> {
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
        EventBus.sendEvent(ServiceStartEvent(startTime))
    }

    fun getSuggest(): Observable<Int> {
        return suggest
    }

    fun getSuggestListSize(): Int {
        return suggestListSize
    }

    fun getSuggestList(): ArrayList<Suggestion> {
        return suggestList
    }

    fun addSuggest(suggest: Suggestion) {
        if (startTime != 0L)
            suggestList.add(suggest)
        suggestListSize++
        this.suggest.onNext(suggestListSize)
    }

    fun removeSuggestItem(position: Int) {
        suggestList.removeAt(position)
        suggestListSize--
        this.suggest.onNext(suggestListSize)
    }

    fun clearData() {
        timeCodeList.clear()
        suggestList.clear()
        userActionList.clear()

        startTime = 0L
        totalDistance = 0L
        suggestListSize = 0
        this.suggest.onNext(suggestListSize)
        EventBus.sendEvent(ServiceStartEvent(startTime))
    }

    fun addUserAction(userAction: UserAction) {
        userActionList[userAction.date.time] = userAction
    }

    fun getUserAction(date: Long): UserAction? {
        return userActionList[date]
    }

    fun getUserActionList(): HashMap<Long, UserAction> {
        return userActionList
    }

    fun removeUserAction(date: Long) {
        userActionList.remove(date)
    }
}

data class ServiceStartEvent(val startTime: Long)