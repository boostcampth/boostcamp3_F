package com.boostcamp.travery.mapservice.savecourse

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.CourseRepository
import com.boostcamp.travery.data.local.db.AppDatabase
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.utils.DateUtils
import com.boostcamp.travery.utils.FileUtils
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class CourseSaveViewModel(application: Application) : BaseViewModel(application) {

    private var title: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var body: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var inputTheme: String = ""
        get() = if (field.isEmpty()) "활동" else field

    val theme = ObservableField<String>("")
    private lateinit var staticMapURL: String
    val url = ObservableField<String>()
    val distance = ObservableField<String>()

    val today = DateUtils.parseDateAsString(System.currentTimeMillis(), "yyyy.MM.dd EEE요일")

    private val courseRepository = CourseRepository.getInstance(
            AppDatabase.getInstance(application).daoCourse(),
            AppDatabase.getInstance(application).daoUserAction(),
            application.filesDir
    )

    fun generateStaticMap(bundle: Bundle?) {
        val timeCodeList = bundle?.getParcelableArrayList(Constants.EXTRA_COURSE_LOCATION_LIST) ?: listOf<TimeCode>()

        val marker = if (timeCodeList.size >= 2) {
            "&markers=color:red%7C${timeCodeList[0].coordinate.latitude},${timeCodeList[0].coordinate.longitude}" +
                    "&markers=color:blue%7C${timeCodeList.last().coordinate.latitude},${timeCodeList.last().coordinate.longitude}"
        } else ""

        val urlPath =
                StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=200x200$marker&scale=2&path=weight:5%7Ccolor:0x02d864ff")

        for (timeCode in timeCodeList) {
            urlPath.append("|${timeCode.coordinate.latitude},${timeCode.coordinate.longitude}")
        }

        urlPath.append("&key=${getApplication<Application>().getString(R.string.google_maps_key)}")

        staticMapURL = urlPath.toString()
        url.set(staticMapURL)

        val dist = bundle?.let { (it.getParcelable<Course>(Constants.EXTRA_COURSE)?.distance)?.div(1000.0).toString() } ?: "0"
        distance.set("총 거리 : ${dist}km")
    }

    private fun makeCoordinateJson(timeCodeList: ArrayList<TimeCode>): JSONObject {
        val coordinates = JSONObject()
        val coordinateItem = JSONArray()
        val colms = JSONObject()

        for (timecode in timeCodeList) {
            val coordinate = JSONObject()
            coordinate.put("lat", timecode.coordinate.latitude)
            coordinate.put("lng", timecode.coordinate.longitude)
            coordinate.put("time", timecode.timeStamp)
            coordinateItem.put(coordinate)
        }

        colms.put("name", timeCodeList[0].timeStamp)
        coordinates.put("colms", colms)
        coordinates.put("coordinate", coordinateItem)

        return coordinates
    }

    fun onSelectItem(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        theme.set(parent.selectedItem.toString())
    }

    fun saveCourseToDatabase(bundle: Bundle?) {
        bundle?.let {
            it.getParcelableArrayList<LatLng>(Constants.EXTRA_COURSE_LOCATION_LIST)
            val mCourse = it.getParcelable<Course>(Constants.EXTRA_COURSE)!!

            Completable.fromAction {
                FileUtils.saveJsonFile(
                        getApplication(), mCourse.startTime.toString(), makeCoordinateJson(
                        it.getParcelableArrayList<TimeCode>(Constants.EXTRA_COURSE_LOCATION_LIST)!!)
                )
            }.doOnComplete {
                val course = Course(
                        title,
                        body,
                        when (theme.get()) {
                            getApplication<Application>().getString(R.string.string_input_theme) -> {
                                inputTheme
                            }
                            else -> {
                                theme.get() ?: ""
                            }
                        },
                        mCourse.startTime,
                        mCourse.endTime,
                        mCourse.distance,
                        mCourse.coordinate,
                        staticMapURL
                )

                addDisposable(
                        courseRepository.updateCourse(course).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                )

                // 코스 저장 이벤트 전달
                EventBus.sendEvent(CourseSaveEvent(course))
            }.subscribe().dispose()
        }
    }

    fun onTitleChange(title: CharSequence) {
        this.title = title.toString()
    }

    fun onBodyChange(body: CharSequence) {
        this.body = body.toString()
    }

    fun onThemeChange(theme: CharSequence) {
        this.inputTheme = theme.toString()
    }
}

data class CourseSaveEvent(val course: Course)