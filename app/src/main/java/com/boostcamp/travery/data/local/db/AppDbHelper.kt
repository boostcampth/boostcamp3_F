package com.boostcamp.travery.data.local.db

import android.content.Context
import com.boostcamp.travery.data.local.AppDatabase
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

class AppDbHelper private constructor(private var appDatabase: AppDatabase) : DbHelper {

    companion object {
        private var INSTANCE: AppDbHelper? = null
        fun getInstance(context: Context): AppDbHelper {
            if (INSTANCE == null) {
                INSTANCE = AppDbHelper(AppDatabase.getInstance(context))
            }
            return INSTANCE as AppDbHelper
        }
    }

    override fun saveCourse(course: Course): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveUserAction(userAction: UserAction): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCourse(course: Course): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCourseList(courseList: List<Course>): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteUserActionList(userAction: List<UserAction>): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllCourse(): Flowable<List<Course>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCourseForKeyword(): Flowable<List<Course>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserActionForKeyword(): Flowable<List<UserAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}