package com.boostcamp.travery.coursedetail

import android.app.Application
import android.location.Geocoder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.useraction.detail.UserActionDeleteEvent
import com.boostcamp.travery.useraction.save.UserActionUpdateEvent
import com.boostcamp.travery.utils.DateUtils
import com.google.android.gms.maps.model.LatLng
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CourseDetailViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var course: Course
    private val courseDetailRepository = Injection.provideCourseRepository(application)
    private val timeCodeList = ArrayList<TimeCode>() //활동 추가를 위한 경로 좌표리스트
    val timeCodeListSize = ObservableInt(0)

    private val userActionList = ObservableArrayList<UserAction>()
    val curUseraction = MutableLiveData<UserAction>()
    val userActionListAdapter = UserActionListAdapter(userActionList)
    val latLngList = MutableLiveData<List<LatLng>>() //저장된 코스를 맵에 보여주기 위한 좌표리스트
    val markerList = MutableLiveData<List<UserAction>>()
    val scrollTo = ObservableInt()
    val totalDistance = ObservableField<String>()
    val userActionHashMap = HashMap<Int, Int>()

    val seekProgress = MutableLiveData<Int>()
    val seekTimeCode = MutableLiveData<TimeCode>()
    val seekListener: OnSeekChangeListener = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams) {
            seekTimeCode.value = timeCodeList[seekParams.progress]
            if (userActionHashMap.containsKey(seekParams.progress)) {
                scrollTo.set(userActionHashMap[seekParams.progress] ?: 0)
            }
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
    }

    init {
        // 활동 수정사항 이벤트 observe
        addDisposable(EventBus.getEvents()
                .ofType(UserActionUpdateEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userActionList.clear()
                    loadUserActionList()
                })

        addDisposable(EventBus.getEvents()
                .ofType(UserActionDeleteEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userActionList.clear()
                    loadUserActionList()
                })
    }

    private var eventListener: ViewModelEventListener? = null

    interface ViewModelEventListener {
        fun onItemClick(item: Any)
    }

    fun init(course: Course) {
        this.course = course

        totalDistance.set(
                when (course.distance >= 1000) {
                    true -> "${String.format("%.2f", course.distance / 1000.0)}km"
                    false -> "${course.distance}m"
                }
        )
        val tempList = ArrayList<LatLng>()
        //저장소로부터 TimeCode리스트를 받아 ViewModel의 TimeCode리스트와 LatLng리스트로 저장
        addDisposable(courseDetailRepository.loadCoordinateListFromJsonFile(course.startTime.toString())
                .flatMap { timeList ->
                    timeCodeList.addAll(timeList)
                    timeCodeListSize.set(timeList.size - 1)
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
                course.title,
                course.body,
                hashTag = course.theme,
                mainImage = DateUtils.parseDateAsString(course.startTime, "yyyy.MM.dd HH:mm") +
                        " ~ " + DateUtils.parseDateAsString(course.endTime, "HH:mm"),
                subImage = "${String.format("%.2f", course.distance / 1000.0)}km",
                latitude = latLngList.value?.let { it[0].latitude } ?: .0,
                longitude = latLngList.value?.let { it[0].longitude } ?: .0
        )
        val endLatLng = latLngList.value?.get(latLngList.value?.size?.minus(1) ?: 0)
                ?: LatLng(0.0, 0.0)
        addDisposable(Single.fromCallable {
            Geocoder(getApplication()).getFromLocation(endLatLng.latitude, endLatLng.longitude, 1)[0]
        }.map {
            val split = it.getAddressLine(0).split(" ")
            UserAction(
                    "코스정보",
                    split.subList(1, split.size).fold("") { acc, s -> "$acc $s" },
                    mainImage = "총 걸린 시간: " + DateUtils.getTotalTime(course.endTime - course.startTime),
                    latitude = latLngList.value?.let { latLng -> latLng[latLng.size - 1].latitude }
                            ?: .0,
                    longitude = latLngList.value?.let { latLng -> latLng[latLng.size - 1].longitude }
                            ?: .0
            )
        }.flatMap {
            Flowable.merge(Flowable.just(start),
                    (courseDetailRepository.getUserActionForCourse(course).flatMap { list -> Flowable.fromIterable(list) }),
                    Flowable.just(it)).toList()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    userActionList.addAll(it)
                    markerList.value = it

                    var userActionPosition = 0
                    latLngList.value?.let { list ->
                        for (pos in 0 until list.size) {
                            if (list[pos].longitude == userActionList[userActionPosition].longitude
                                    && list[pos].latitude == userActionList[userActionPosition].latitude) {
                                userActionHashMap[pos] = userActionPosition
                                userActionPosition++
                            }
                        }
                    }
                }, {})
        )
    }


    /**
     * 맵상의 마커가 클릭 되었을때 현재 선택된 아이템 포지션과 활동 마커인지를 체크
     */
    fun markerClick(position: Int) {
        scrollTo.set(position)
        seekTimeCode.value = TimeCode(LatLng(userActionList[position].latitude, userActionList[position].longitude))
        userActionHashMap.forEach {
            if (it.value == position) {
                seekProgress.value = it.key
            }
        }
        //활동 마커에 대해서만 바텀 뷰를 보여주기 위함
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



