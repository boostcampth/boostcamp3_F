package com.boostcamp.travery.data

import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable

interface CourseDetailDataSource {

    fun getUserActionForCourse(course: Course): Flowable<List<UserAction>>


}