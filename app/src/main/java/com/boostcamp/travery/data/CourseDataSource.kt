package com.boostcamp.travery.data

import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable
import org.intellij.lang.annotations.Flow
import org.json.JSONObject
import java.io.File

interface CourseDataSource {


    fun saveCourse(course: Course): Observable<Boolean>

    fun saveUserAction(userAction: UserAction): Observable<Boolean>

    fun deleteCourse(course: Course): Observable<Boolean>

    fun deleteCourseList(courseList: List<Course>): Observable<Boolean>

    fun deleteUserAction(userAction: UserAction): Observable<Boolean>

    fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean>

    fun updateCourse(course: Course): Observable<Boolean>

    fun updateUserAction(userAction: UserAction): Observable<Boolean>

    fun getAllCourse(): Flowable<List<Course>>

    fun getAllUserAction(): Flowable<List<UserAction>>

    fun getCourseForKeyword(keyword: String): Flowable<List<Course>>

    fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>>

    fun getUserActionForCourse(course: Course): Flowable<List<UserAction>>

    fun saveJsonFile(fileName: String, jsonObj: JSONObject)

    fun loadCoordinateListFromJsonFile(fileName: String): Flowable<List<TimeCode>>

    fun deleteCourseFile(fileName: String)
}

