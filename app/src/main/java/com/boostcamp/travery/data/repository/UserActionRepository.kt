package com.boostcamp.travery.data.repository

import android.content.Context
import com.boostcamp.travery.data.UserActionDataSource
import com.boostcamp.travery.data.local.source.UserActionLocalDataSource
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

class UserActionRepository private constructor(context: Context) : UserActionDataSource {
    private val dataSource by lazy { UserActionLocalDataSource.getInstance(context) }

    companion object {
        @Volatile
        private var INSTANCE: UserActionRepository? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserActionRepository(context).also { INSTANCE = it }
        }
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return dataSource.getAllUserAction()
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        return dataSource.getUserActionForKeyword(keyword)
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return dataSource.getUserActionForCourse(course)
    }

    override fun insertUserAction(userAction: UserAction): Observable<Boolean> {
        return dataSource.insertUserAction(userAction)
    }

    override fun insertUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return dataSource.insertUserActionList(userActionList)
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        return dataSource.deleteUserAction(userAction)
    }

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return dataSource.deleteUserActionList(userActionList)
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        return dataSource.updateUserAction(userAction)
    }
}