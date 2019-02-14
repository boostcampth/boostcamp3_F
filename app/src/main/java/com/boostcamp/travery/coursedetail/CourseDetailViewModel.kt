package com.boostcamp.travery.coursedetail

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.CourseRepository
import com.boostcamp.travery.data.local.db.AppDatabase
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CourseDetailViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var course: Course
    private val courseDetailRepository =
            CourseRepository.getInstance(
                    AppDatabase.getInstance(application).daoCourse(),
                    AppDatabase.getInstance(application).daoUserAction(),
                    application.filesDir
            )
    private val timeCodeList = ArrayList<TimeCode>() //활동 추가를 위한 경로 좌표리스트
    private val userActionList = ObservableArrayList<UserAction>()
    val curUseraction = MutableLiveData<UserAction>()
    val userActionListAdapter = UserActionListAdapter(userActionList)
    val latLngList = MutableLiveData<List<LatLng>>() //저장된 코스를 맵에 보여주기 위한 좌표리스트
    val markerList = MutableLiveData<List<UserAction>>() //지
    val isAnimated = ObservableBoolean()
    val scrollTo = ObservableInt()
    private var eventListener: ViewModelEventListener? = null

    interface ViewModelEventListener {
        fun onItemClick(item: Any)
    }

    fun init(course: Course) {
        this.course = course
        val tempList = ArrayList<LatLng>()
        //저장소로부터 TimeCode리스트를 받아 ViewModel의 TimeCode리스트와 LatLng리스트로 저장
        addDisposable(courseDetailRepository.loadCoordinateListFromJsonFile(course.startTime.toString())
                .flatMap { timeList ->
                    timeCodeList.addAll(timeList)
                    Flowable.fromIterable(timeList)
                }
                .map { it -> it.coordinate }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tempList.add(it)
                }, {}, {
                    latLngList.value = tempList
                    loadUserActionList()
                })
        )

        userActionListAdapter.onItemClickListener = { item: Any -> onItemClick(item as UserAction) }

    }

    //코스에 대한 활동 리스트를 가져옴
    private fun loadUserActionList() {
        val start = UserAction(
                "산뜻한 출발",
                latitude = latLngList.value!![0].latitude,
                longitude = latLngList.value!![0].longitude
        )
        val end = UserAction(
                "도오착",
                latitude = latLngList.value!![latLngList.value!!.size - 1].latitude,
                longitude = latLngList.value!![latLngList.value!!.size - 1].longitude
        )

        addDisposable(
                Flowable.merge(Flowable.just(start),
                        (courseDetailRepository.getUserActionForCourse(course).flatMap { list -> Flowable.fromIterable(list) }), Flowable.just(end))
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { it ->
                            userActionList.addAll(it)
                            markerList.value = it
                        })
    }


    /**
     * 맵상의 마커가 클릭 되었을때 현재 선택된 아이템 포지션과 활동 마커인지를 체크
     */
    fun markerClick(position: Int) {
        scrollTo.set(position)
        isAnimated.set(false)
        //활동 마커에 대해서만 바텀 뷰를 보여주기 위함
    }

    fun mapClick() {
        //마커 클릭 하지 않았음을 알림.
        isAnimated.set(!isAnimated.get())
    }

    fun updateCurUseraction(position: Int) {
        curUseraction.value = markerList.value?.get(position)
    }

    private fun onItemClick(userAction: UserAction) {
        eventListener?.onItemClick(userAction)
    }

    fun setEventListener(eventListener: ViewModelEventListener) {
        this.eventListener = eventListener
    }
}



