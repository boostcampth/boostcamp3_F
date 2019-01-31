package com.boostcamp.travery.mapservice.savecourse

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import com.boostcamp.travery.base.BaseViewModel
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants


class CourseSaveViewModel(application: Application) : BaseViewModel(application) {

    private var title: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var body: String = ""
        get() = if (field.isEmpty()) "활동" else field

    val theme = ObservableField<String>("")

    fun onSelectItem(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        theme.set(parent.selectedItem.toString())
        Log.d("lolote", theme.get())
    }

    fun saveCourseToDatabase(bundle: Bundle?) {
        val imageFilePath = requestStaticMap() // 비동기 호출 예상

        Log.d("lolote", bundle?.getLong(Constants.EXTRA_ROUTE_START_TIME, System.currentTimeMillis()).toString())
        Log.d("lolote", bundle?.getLong(Constants.EXTRA_ROUTE_END_TIME, System.currentTimeMillis()).toString())
        Log.d("lolote", bundle?.getLong(Constants.EXTRA_ROUTE_DISTANCE, 0L).toString())
        Log.d("lolote", bundle?.getString(Constants.EXTRA_ROUTE_COORDINATE, "").toString())

        // DB 저장 코드
//        with(intent) {
//            Thread(Runnable {
//                DataBase.getDataBase(this@CourseSaveActivity)
//                        .daoCourse()
//                        .insertCourse(Course(
//                                et_title.text.toString(),
//                                et_content.text.toString(),
//                                et_selected_theme.text.toString(),
//                                getLongExtra(Constants.EXTRA_ROUTE_START_TIME, System.currentTimeMillis()),
//                                getLongExtra(Constants.EXTRA_ROUTE_END_TIME, System.currentTimeMillis()),
//                                getLongExtra(Constants.EXTRA_ROUTE_DISTANCE, 0L),
//                                getStringExtra(Constants.EXTRA_ROUTE_COORDINATE),
//                                imageFilePath))
//            }).start()
//        }
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
}