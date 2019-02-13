package com.boostcamp.travery.mapservice

import android.app.Application
import android.location.Location
import android.util.Log
import com.boostcamp.travery.base.BaseViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.data.model.TimeCode
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class TrackingViewModel(application: Application) : BaseViewModel(application) {

    private val mapTrackingRepository = MapTrackingRepository.getInstance()
    var secondString = ObservableField<String>("")
    val isService = ObservableField<Boolean>(false)
    val curLocation = MutableLiveData<LatLng>()
    val suggestionList = MutableLiveData<ArrayList<Suggestion>>()
    val totalDistance by lazy { mapTrackingRepository.getTotalDistance() }
    val startTime by lazy { mapTrackingRepository.getStartTime() }
    val userActionLocateList by lazy { mapTrackingRepository.getUsetActionLocateList() }

    init {
        addDisposable(
                mapTrackingRepository.getTimeCode().subscribe {
                    curLocation.value = it.coordinate
                }
        )

        addDisposable(
                mapTrackingRepository.getSuggest().subscribe {
                    Log.d("lolocation", mapTrackingRepository.getSuggestList().toString())
                    suggestionList.value = mapTrackingRepository.getSuggestList()
                }
        )

        addDisposable(
                mapTrackingRepository.getSecond()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            secondString.set(setIntToTime(it))
                        })

        if (mapTrackingRepository.getStartTime() != 0L)
            isService.set(true)
    }

    private fun setIntToTime(timeInt: Int): String {

        var min = timeInt / 60
        val hour = min / 60
        val sec = timeInt % 60
        min %= 60

        return "${String.format("%02d", hour)}:${String.format("%02d", min)}:${String.format("%02d", sec)}"
    }

    fun getTimeCodeList(): ArrayList<TimeCode> {
        return mapTrackingRepository.getTimeCodeList()
    }

    fun getSuggestList(): ArrayList<Suggestion> {
        return mapTrackingRepository.getSuggestList()
    }

    fun removeSuggestItem(position: Int) {
        mapTrackingRepository.removeSuggestItem(position)
    }

    fun getIsServiceState(): Boolean {
        return isService.get() ?: false
    }

    fun setIsServiceState(boolean: Boolean) {
        isService.set(boolean)
    }

    fun addUserActionLocate(locate: LatLng){
        mapTrackingRepository.addUserActionLocate(locate)
    }
}