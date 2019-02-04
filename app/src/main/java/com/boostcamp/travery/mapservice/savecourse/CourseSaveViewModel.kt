package com.boostcamp.travery.mapservice.savecourse

import android.app.Application
import android.os.Bundle
import android.view.View
import com.boostcamp.travery.base.BaseViewModel
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.utils.FileUtils
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder


class CourseSaveViewModel(application: Application) : BaseViewModel(application) {

    private var title: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var body: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var inputTheme: String = ""
        get() = if (field.isEmpty()) "활동" else field

    val theme = ObservableField<String>("")
    lateinit var staticMapURL: String

    private fun makeCoordinateJson(locationList: ArrayList<LatLng>, timeList: ArrayList<String>): JSONObject {

        val coordinates = JSONObject()
        val coordinateItem = JSONArray()
        val colms = JSONObject()
        val urlPath =
            StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=100x100&path=color:0x0000ff|weight:5")
        for (i in 0 until locationList.size) {
            val coordinate = JSONObject()
            coordinate.put("lat", locationList[i].latitude)
            coordinate.put("lng", locationList[i].longitude)
            //TODO timeList Long형으로 바꿔야합니다.
            coordinate.put("time", timeList[i].toLong())
            coordinateItem.put(coordinate)
            urlPath.append("|${locationList[i].latitude},${locationList[i].longitude}")
        }
        urlPath.append("&key=${getApplication<Application>().getString(R.string.google_maps_key)}")

        colms.put("name", timeList[0])
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
            it.getParcelableArrayList<LatLng>(Constants.EXTRA_ROUTE_LOCATION_LIST)
            val mCourse = it.getParcelable<Course>(Constants.EXTRA_ROUTE)

            Single.just(
                FileUtils.saveJsonFile(
                    getApplication(), mCourse!!.startTime.toString(), makeCoordinateJson(
                        it.getParcelableArrayList<LatLng>(Constants.EXTRA_ROUTE_LOCATION_LIST)!!,
                        it.getStringArrayList(Constants.EXTRA_ROUTE_TIME_LIST)!!
                    )
                )
            ).doAfterSuccess {
                addDisposable(
                    repository.saveCourse(
                        Course(
                            title,
                            body,
                            when (theme.get()) {
                                getApplication<Application>().getString(R.string.string_input_theme) -> {
                                    inputTheme
                                }
                                else -> {
                                    theme.get()?:""
                                }
                            },
                            mCourse.startTime,
                            mCourse.endTime,
                            mCourse.distance,
                            mCourse.coordinate,
                            staticMapURL
                        )
                    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                )
            }.subscribe()
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