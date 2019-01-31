package com.boostcamp.travery.data

import android.util.Log
import com.boostcamp.travery.data.local.db.DbHelper
import com.boostcamp.travery.data.local.prefs.PreferHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.remote.ApiHelper
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

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
        return dbHelper.getAllCourse()
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return dbHelper.getAllUserAction()
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

    /**
     * 더미데이터 DB insert
     * 한번만 최초 실행해야함.
     */
    override fun insertDummyData() {
        for (i in 0..10) {
            dbHelper.saveCourse(
                    Course(
                            "부스트 캠프 안드로이드조",
                            "여기 해시태그 자리가 아니었나?",
                            "부스트캠프",
                            System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (i + i % 2),
                            System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (i + i % 2) + 1000 * 60,
                            100,
                            "",
                            ""
                    )
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }

        dbHelper.getAllCourse()
                .subscribeOn(Schedulers.io())
                .subscribe { list ->
                    list.forEach {
                        val data = ArrayList<UserAction>()
                        val key = it.startTime
                        for (i in 0..10) {
                            data.add(
                                    UserAction(
                                            "부스트 캠프 안드로이드조",
                                            "여기 해시태그 자리가 아니었나?",
                                            Date(System.currentTimeMillis() - 1000 * 60 * 60 * i),
                                            "해시태그!!!!!",
                                            "",
                                            "",
                                            0,
                                            0,
                                            key
                                    )
                            )
                        }
                        dbHelper.saveUserActionList(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                    }
                }.also {  }
    }
}