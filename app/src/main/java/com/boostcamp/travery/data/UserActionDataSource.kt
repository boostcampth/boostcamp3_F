package com.boostcamp.travery.data

import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

interface UserActionDataSource {

    fun getAllUserAction(): Flowable<List<UserAction>>

    fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>>

    fun getUserActionForCourse(course: Course): Flowable<List<UserAction>>

    fun insertUserAction(userAction: UserAction): Observable<Boolean>

    fun insertUserActionList(userActionList: List<UserAction>): Observable<Boolean>

    fun deleteUserAction(userAction: UserAction): Observable<Boolean>

    fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean>

    fun updateUserAction(userAction: UserAction): Observable<Boolean>

}