package com.boostcamp.travery.mapservice

import android.app.Application
import android.widget.BaseAdapter
import androidx.databinding.ObservableBoolean
import com.boostcamp.travery.base.BaseViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.repository.MapTrackingRepository
import com.boostcamp.travery.data.repository.ServiceStartEvent
import com.boostcamp.travery.eventbus.EventBus
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class TrackingViewModel(application: Application) : BaseViewModel(application) {

    private val mapTrackingRepository = MapTrackingRepository.getInstance()
    var secondString = ObservableField<String>("")
    val isService = ObservableBoolean(false)
    val curLocation = MutableLiveData<LatLng>()
    var suggestCountString = ObservableField<String>("0")
    val totalDistance by lazy { mapTrackingRepository.getTotalDistance() }
    val startTime by lazy { mapTrackingRepository.getStartTime() }
    val userActionLocateList by lazy { mapTrackingRepository.getUserActionLocateList() }
    private var suggestAdapter: BaseAdapter? = null

    init {

        addDisposable(
                mapTrackingRepository.getTimeCode().subscribe {
                    curLocation.value = it.coordinate
                }
        )

        suggestCountString.set(mapTrackingRepository.getSuggestListSize().toString())

        addDisposable(
                mapTrackingRepository.getSuggest().subscribe {
                    //suggestionList.value = mapTrackingRepository.getSuggestList()
                    suggestCountString.set(it.toString())
                    suggestAdapter?.notifyDataSetChanged()
                }
        )

        addDisposable(
                mapTrackingRepository.getSecond()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            secondString.set(setIntToTime(it))
                        }
        )

        addDisposable(
                EventBus.getEvents().ofType(ServiceStartEvent::class.java).subscribe {
                    if (it.startTime != 0L) {
                        saveInitCourse(it.startTime)
                        isService.set(true)
                    } else {
                        isService.set(false)
                        secondString.set("")
                    }
                }
        )
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

    private fun saveInitCourse(startTime: Long) {
        AppDataManager(getApplication(), AppDbHelper.getInstance(getApplication())).saveCourse(
                Course(startTime = startTime)
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeSuggestItem(position: Int) {
        mapTrackingRepository.removeSuggestItem(position)
    }

    fun getIsServiceState(): Boolean {
        return isService.get()
    }

    fun addUserActionLocate(locate: LatLng) {
        mapTrackingRepository.addUserActionLocate(locate)
    }

    fun getSuggestAdapter(): BaseAdapter {
        suggestAdapter = SuggestListAdapter(mapTrackingRepository.getSuggestList())
        return suggestAdapter!!
    }
}