package com.boostcamp.travery.coursedetail

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
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
    val curUseraction = MutableLiveData<UserAction>()
    val latLngList = MutableLiveData<List<LatLng>>() //저장된 코스의 좌표리스트
    val markerList = MutableLiveData<List<UserAction>>() //지
    val leftAdapter = UserActionLeftListAdapter(leftActionList)
    val topAdapter = UserActionTopListAdapter(topActionList)
    val isSelected = ObservableBoolean() //마크가 선택되었는지 체크


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
                }, {}, { latLngList.value = tempList
                    loadUserActionList()})
        )

        leftAdapter.onItemClickListener = { onUserActionClicked(it as Int) }
    }

    //코스에 대한 활동 리스트를 가져옴
    private fun loadUserActionList() {

        addDisposable(repository.getUserActionForCourse(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //TODO 임시로 출발지와 도착지 title 설정
                    val arr: Array<UserAction?> = arrayOf(
                            UserAction(
                                    "산뜻한 출발",
                                    latitude = latLngList.value!![0].latitude,
                                    longitude = latLngList.value!![0].longitude
                            ),
                            UserAction(
                                    "도오착",
                                    latitude = latLngList.value!![latLngList.value!!.size - 1].latitude,
                                    longitude = latLngList.value!![latLngList.value!!.size - 1].longitude
                            )
                    )
                    leftActionList.addAll(arr)
                    topActionList.addAll(arr)
                    topActionList.addAll(1, it)
                    leftActionList.addAll(0, it)
                    markerList.value = topActionList
                })
    }

    fun onUserActionClicked(position: Int) {
        if (position == leftAdapter.itemCount - 1) {
            scrollTo.set(0)
            curUseraction.value = topActionList[0]
        } else {
            scrollTo.set(position + 1)
            curUseraction.value = topActionList[position + 1]
        }
        scrollTo.notifyChange()
    }

    fun updateCurUseraction(position: Int) {
        curUseraction.value = topActionList[position]
    }

    /**
     * 맵상의 마커가 클릭 되었을때 현재 선택된 아이템 포지션과 활동 마커인지를 체크
     */
    fun markerClick(position: Int) {
        scrollTo.set(position)
        curUseraction.value = topActionList[position]

        //활동 마커에 대해서만 바텀 뷰를 보여주기 위함
        if (position == 0 || position == topActionList.size - 1) {
            isSelected.set(false)
        } else {
            isSelected.set(true)
        }
    }

    fun mapClick(){
        //마커 클릭 하지 않았음을 알림.
        isSelected.set(false)
    }


}



