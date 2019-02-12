package com.boostcamp.travery.data.repository

import com.boostcamp.travery.data.UserActionDataSource
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

class UserActionRepository private constructor(private val userActionDataSource: UserActionDataSource) :
        UserActionDataSource {
    private var cachedUserActionList = ArrayList<UserAction>()
    private var isDirty = false

    companion object {
        @Volatile
        private var INSTANCE: UserActionRepository? = null

        @JvmStatic
        fun getInstance(userActionDataSource: UserActionDataSource) = INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                            ?: UserActionRepository(userActionDataSource).also { INSTANCE = it }
                }
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return Flowable.just(cachedUserActionList.isEmpty()).flatMap { empty ->
            if (!empty && !isDirty) {
                Flowable.just(cachedUserActionList)
            } else {
                userActionDataSource.getAllUserAction().doOnNext {
                    cachedUserActionList.clear()
                    cachedUserActionList.addAll(it)
                    isDirty = false
                }
            }
        }
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        return userActionDataSource.getUserActionForKeyword(keyword)
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return userActionDataSource.getUserActionForCourse(course)
    }

    override fun insertUserAction(userAction: UserAction): Observable<Boolean> {
        isDirty = true
        return userActionDataSource.insertUserAction(userAction)
    }

    override fun insertUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        isDirty = true
        return userActionDataSource.insertUserActionList(userActionList)
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        isDirty = true
        return userActionDataSource.deleteUserAction(userAction)
    }

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        isDirty = true
        return userActionDataSource.deleteUserActionList(userActionList)
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        isDirty = true
        return userActionDataSource.updateUserAction(userAction)
    }
}