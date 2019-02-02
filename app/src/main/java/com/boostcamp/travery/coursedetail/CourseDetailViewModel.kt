package com.boostcamp.travery.coursedetail

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CourseDetailViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var course: Course
    private val leftActionList = ObservableArrayList<UserAction?>()
    private val topActionList = ObservableArrayList<UserAction?>()
    private val timeCodeList = ArrayList<TimeCode>()
    val latLng = MutableLiveData<List<LatLng>>()
    val leftAdapter = UserActionLeftListAdapter(leftActionList)
    val topAdapter = UserActionTopListAdapter(topActionList)


    fun init(course: Course) {
        this.course = course
        val latLngList = ArrayList<LatLng>()
        //저장소로부터 TimeCode리스트를 받아 ViewModel의 TimeCode리스트와 LatLng리스트로 저장
        addDisposable(repository.loadCourseCoordinate(course.startTime.toString())
                .flatMap { timeList ->
                    timeCodeList.addAll(timeList)
                    Flowable.fromIterable(timeList)
                }
                .flatMap { timeCode -> Flowable.just(LatLng(timeCode.latitude, timeCode.longitude)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ latLngList.add(it) }, {}, { latLng.value = latLngList })
        )
    }

    //코스에 대한 활동 리스트를 가져옴
    fun loadUserAtionList() {
        addDisposable(repository.getUserActionForCourse(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val arr: Array<UserAction?> = arrayOf(null, null)
                    leftActionList.addAll(arr)
                    topActionList.addAll(arr)
                    topActionList.addAll(1, it)
                    leftActionList.addAll(0, it)
                })
    }

}



