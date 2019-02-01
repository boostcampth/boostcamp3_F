package com.boostcamp.travery.coursedetail

import android.app.Application
import android.util.Log
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.Course
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CourseDetailViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var course: Course
    val leftAdapter = UserActionLeftListAdapter()
    val topAdapter = UserActionTopListAdapter()

    fun loadUserAtionList() {
        addDisposable(repository.getUserActionForCourse(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    leftAdapter.addItems(it)
                    leftAdapter.addItem(null)
                    leftAdapter.addItem(null)
                    topAdapter.addItem(null)
                    topAdapter.addItems(it)
                    topAdapter.addItem(null)
                })
    }

    fun setCourse(course: Course) {
        this.course = course
    }
}

