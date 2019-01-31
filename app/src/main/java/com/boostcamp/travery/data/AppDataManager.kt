package com.boostcamp.travery.data

import com.boostcamp.travery.data.local.db.DbHelper
import com.boostcamp.travery.data.local.prefs.PreferHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.remote.ApiHelper
import io.reactivex.Flowable
import io.reactivex.Observable

class AppDataManager(private val dbHelper: DbHelper) : DataManager {
    private lateinit var preferHelper: PreferHelper
    private lateinit var apiHelper: ApiHelper

    constructor(
        dbHelper: DbHelper,
        apiHelper: ApiHelper
    ) : this(dbHelper) {
        this.apiHelper = apiHelper
    }

    constructor(
        dbHelper: DbHelper,
        preferHelper: PreferHelper,
        apiHelper: ApiHelper
    ) : this(dbHelper) {
        this.preferHelper = preferHelper
        this.apiHelper = apiHelper
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

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
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

    override fun getCourseForKeyword(keyword: String): Flowable<List<Course>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}