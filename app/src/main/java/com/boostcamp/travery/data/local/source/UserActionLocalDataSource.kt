package com.boostcamp.travery.data.local.source

import android.content.Context
import com.boostcamp.travery.data.UserActionDataSource
import com.boostcamp.travery.data.local.db.AppDatabase
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

class UserActionLocalDataSource private constructor(context: Context) : UserActionDataSource {
    private val userActionDao by lazy { AppDatabase.getInstance(context).daoUserAction() }

    companion object {
        @Volatile
        private var INSTANCE: UserActionLocalDataSource? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserActionLocalDataSource(context).also { INSTANCE = it }
        }
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            userActionDao.loadAll()
        }
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            userActionDao.searchUserActionForKeyword(keyword)
        }
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            userActionDao.loadUserActionForCourse(course.startTime)
        }
    }

    override fun insertUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.insert(userAction)
            true
        }
    }

    override fun insertUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.insert(userActionList)
            true
        }
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.delete(userAction)
            true
        }
    }

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.delete(userActionList)
            true
        }
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.update(userAction)
            true
        }
    }
}