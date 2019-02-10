package com.boostcamp.travery.mapservice.savecourse

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.eventbus.EventBus
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
    lateinit var staticMapURL: String

    private fun makeCoordinateJson(timeCodeList: ArrayList<TimeCode>): JSONObject {

        val coordinates = JSONObject()
        val coordinateItem = JSONArray()
        val colms = JSONObject()
        val urlPath =
                StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=100x100&path=color:0x0000ff|weight:5")
        for (timecode in timeCodeList) {
            val coordinate = JSONObject()
            coordinate.put("lat", timecode.coordinate.latitude)
            coordinate.put("lng", timecode.coordinate.longitude)
            coordinate.put("time", timecode.timeStamp)
            coordinateItem.put(coordinate)

            urlPath.append("|${timecode.coordinate.latitude},${timecode.coordinate.longitude}")
        }
        urlPath.append("&key=${getApplication<Application>().getString(R.string.google_maps_key)}")

        colms.put("name", timeCodeList[0].timeStamp)
        coordinates.put("colms", colms)
        coordinates.put("coordinate", coordinateItem)

        staticMapURL = urlPath.toString()

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
                        it.getParcelableArrayList<TimeCode>(Constants.EXTRA_COURSE_LOCATION_LIST)!!
                )
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
                        repository.updateCourse(course).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
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