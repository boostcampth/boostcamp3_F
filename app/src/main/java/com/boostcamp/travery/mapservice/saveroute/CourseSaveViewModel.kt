package com.boostcamp.travery.mapservice.saveroute

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.databinding.ObservableField
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BaseObservable
import java.util.*
import androidx.databinding.adapters.TextViewBindingAdapter.setText

class CourseSaveViewModel(application: Application) : AndroidViewModel(application){

    private var title: String = ""
        get() = if (field.isEmpty()) "활동" else field

    private var body: String = ""
        get() = if (field.isEmpty()) "활동" else field

    fun save() {
        //TODO 저장
        Log.d("lolote",title)
        Log.d("lolote",body)
    }

    private fun saveCourseToDatabase() {
        val imageFilePath = requestStaticMap() // 비동기 호출 예상

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
        Log.d("lolote",title.toString())
    }

    fun onBodyChange(body: CharSequence) {
        this.body = body.toString()
        Log.d("lolote",body.toString())
    }
}