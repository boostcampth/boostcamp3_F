package com.boostcamp.travery.data.local.source

import com.boostcamp.travery.data.CourseDetailDataSource
import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable

class CourseDetailLocalDataSource private constructor(private val userActionDao: UserActionDao):CourseDetailDataSource {


    companion object {
        private var INSTANCE:CourseDetailLocalDataSource?=null

        @JvmStatic
        fun getInstance(userActionDao: UserActionDao)= INSTANCE?: synchronized(this){
            INSTANCE?:CourseDetailLocalDataSource(userActionDao).also { INSTANCE=it }
        }
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            userActionDao.loadUserActionForCourse(course.startTime)
        }
    }
}