package com.boostcamp.travery.mapservice.savecourse

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import com.boostcamp.travery.base.BaseViewModel
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDatabase_Impl
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.local.db.DbHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.utils.FileUtils
import com.google.android.gms.maps.model.LatLng
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

    private fun makeCoordinateJson(locationList: ArrayList<LatLng>, timeList: ArrayList<String>): JSONObject {

        //path=color:0x0000ff|weight:5|40.737102,-73.990318|40.749825,-73.987963|40.752946,-73.987384|40.755823,-73.986397
        val coordinates = JSONObject()
        val coordinateItem = JSONArray()
        val colms = JSONObject()
        var urlPath:StringBuffer
        for (i in 0..(locationList.size - 1)) {
            val coordinate = JSONObject()
            coordinate.put("lat", locationList[i].latitude)
            coordinate.put("lng", locationList[i].longitude)
            coordinate.put("time", timeList[i])
            coordinateItem.put(coordinate)
        }
        colms.put("name", timeList[0])
        coordinates.put("colms", colms)
        coordinates.put("coordinate", coordinateItem)

        Log.d("lolot", coordinates.toString())

        return coordinates
    }

    fun onSelectItem(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        theme.set(parent.selectedItem.toString())
    }

    fun saveCourseToDatabase(bundle: Bundle?) {
        val imageFilePath = requestStaticMap() // 비동기 호출 예상

        bundle?.let {
            it.getParcelableArrayList<LatLng>(Constants.EXTRA_ROUTE_LOCATION_LIST)
            val mCourse = it.getParcelable<Course>(Constants.EXTRA_ROUTE)

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
                                theme.get()
                            }
                        },
                        mCourse!!.startTime,
                        mCourse.endTime,
                        mCourse.distance,
                        mCourse.coordinate,
                        mCourse.mapImage
                    )
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            )

            FileUtils.saveJsonFile(
                getApplication(), mCourse.startTime.toString(), makeCoordinateJson(
                    it.getParcelableArrayList<LatLng>(Constants.EXTRA_ROUTE_LOCATION_LIST)!!,
                    it.getStringArrayList(Constants.EXTRA_ROUTE_TIME_LIST)!!
                )
            )
        }
    }

    private fun requestStaticMap(): String {
        // 스태틱맵을 요청하여 이미지 파일 저장 후 저장 경로 리턴
        return ""
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