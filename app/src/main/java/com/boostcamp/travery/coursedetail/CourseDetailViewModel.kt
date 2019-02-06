package com.boostcamp.travery.coursedetail

import android.app.Application
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
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
    private val leftActionList = ObservableArrayList<UserAction>()
    private val topActionList = ObservableArrayList<UserAction>()
    private val timeCodeList = ArrayList<TimeCode>()
    val scrollTo = ObservableInt()
    val curLatLng=MutableLiveData<LatLng>()
    val latLngList = MutableLiveData<List<LatLng>>()
    val markerList = MutableLiveData<List<UserAction>>()
    val leftAdapter = UserActionLeftListAdapter(leftActionList)
    val topAdapter = UserActionTopListAdapter(topActionList)


    fun init(course: Course) {
        this.course = course
        val tempList = ArrayList<LatLng>()
        //저장소로부터 TimeCode리스트를 받아 ViewModel의 TimeCode리스트와 LatLng리스트로 저장
        addDisposable(repository.loadCourseCoordinate(course.startTime.toString())
                .flatMap { timeList ->
                    timeCodeList.addAll(timeList)
                    Flowable.fromIterable(timeList)
                }
                .flatMap { timeCode -> Flowable.just(timeCode.coordinate) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tempList.add(it)
                }, {}, { latLngList.value = tempList })
        )

        leftAdapter.onItemClickListener = { onUserActionClicked(it as Int) }
    }

    //코스에 대한 활동 리스트를 가져옴
    fun loadUserActionList() {
        addDisposable(repository.getUserActionForCourse(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val arr: Array<UserAction?> = arrayOf(
                            UserAction("산뜻한 출발",latitude = latLngList.value!![0].latitude,longitude = latLngList.value!![0].longitude),
                            UserAction("도오착", latitude = latLngList.value!![latLngList.value!!.size-1].latitude,longitude = latLngList.value!![latLngList.value!!.size-1].longitude)
                    )
                    leftActionList.addAll(arr)
                    topActionList.addAll(arr)
                    topActionList.addAll(1, it)
                    leftActionList.addAll(0, it)
                    markerList.value=topActionList
                })
    }

    fun onUserActionClicked(position: Int) {
        if (position == leftAdapter.itemCount - 1) {
            scrollTo.set(0)
            curLatLng.value= LatLng(topActionList[0].latitude,topActionList[0].longitude)
        } else {
            scrollTo.set(position + 1)
            curLatLng.value= LatLng(topActionList[position+1].latitude,topActionList[position+1].longitude)
        }
        scrollTo.notifyChange()
    }

    fun updatePosition(position: Int){
        scrollTo.set(position)
        curLatLng.value= LatLng(topActionList[position].latitude,topActionList[position].longitude)
    }
}



