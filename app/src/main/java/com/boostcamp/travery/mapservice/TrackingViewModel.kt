package com.boostcamp.travery.mapservice

import android.app.Application
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import androidx.databinding.ObservableBoolean
import com.boostcamp.travery.base.BaseViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.R
import com.boostcamp.travery.data.CourseRepository
import com.boostcamp.travery.data.local.db.AppDatabase
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.repository.MapTrackingRepository
import com.boostcamp.travery.data.repository.ServiceStartEvent
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.utils.DateUtils
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class TrackingViewModel(application: Application) : BaseViewModel(application) {

    private val mapTrackingRepository = MapTrackingRepository.getInstance()
    private val courseRepository = CourseRepository.getInstance(
            AppDatabase.getInstance(application).daoCourse(),
            AppDatabase.getInstance(application).daoUserAction(),
            application.filesDir
    )
    private var distanceTxt = "0m"

    var secondString = ObservableField<String>("")
    val isService = ObservableBoolean(false)
    val curLocation = MutableLiveData<Location>()
    var suggestCountString = ObservableField<String>("0")
    var startTime: Long = mapTrackingRepository.getStartTime()
    val userActionList by lazy { mapTrackingRepository.getUserActionList() }
    private var suggestAdapter: BaseAdapter? = null
    var talkString = ObservableField<String>()
    private var talkCnt = 0
    var isFindGPS = ObservableBoolean(false)

    init {
        talkString.set(application.getString(R.string.string_mapservice_gps))

        addDisposable(
                mapTrackingRepository.getTimeCode().subscribe {
                    curLocation.value = it
                    talkCnt++
                    if (talkCnt > 1) {
                        talkString.set(application.getString(R.string.string_mapservice_start))
                        isFindGPS.set(true)
                    }
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

        /*addDisposable(
            Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val rightNow = Date()
                    nowTime.set(
                        DateUtils.parseDateAsString(
                            rightNow,
                            "yyyy년 MM월 dd일\n  a h시 mm분 ss초\n   오늘의 기록을 시작해보세요."
                        )
                    )
                }
        )*/
        //거리 observe
        /*addDisposable(
                mapTrackingRepository.getDistance().subscribe {

                }
        )*/

        addDisposable(
                EventBus.getEvents().ofType(ServiceStartEvent::class.java).subscribe {
                    startTime = it.startTime

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

    fun getTotalDistance(): Long{
        return mapTrackingRepository.getTotalDistance()
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
        courseRepository.saveCourse(
                Course(startTime = startTime)
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun deleteUserAction(date: Long) {
        getUserAction(date)?.let {
            mapTrackingRepository.removeUserAction(date)
        }
    }

    fun removeSuggestItem(position: Int) {
        mapTrackingRepository.removeSuggestItem(position)
    }

    fun getIsServiceState(): Boolean {
        return isService.get()
    }

    fun addUserAction(userAction: UserAction) {
        mapTrackingRepository.addUserAction(userAction)
    }

    fun getUserAction(date: Long): UserAction? {
        return mapTrackingRepository.getUserAction(date)
    }

    fun getSuggestAdapter(): BaseAdapter {
        suggestAdapter = SuggestListAdapter(mapTrackingRepository.getSuggestList())
        return suggestAdapter!!
    }

}