package com.boostcamp.travery.data.local.source

import com.boostcamp.travery.data.CourseDataSource
import com.boostcamp.travery.data.local.db.dao.CourseDao
import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.utils.NewFileUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import org.json.JSONObject
import java.io.File

class CourseLocalDataSource private constructor(
    private val courseDao: CourseDao,
    private val userActionDao: UserActionDao,
    private val file: File
) : CourseDataSource {

    companion object {
        @Volatile
        private var INSTANCE: CourseLocalDataSource? = null

        @JvmStatic
        fun getInstance(courseDao: CourseDao, userActionDao: UserActionDao, file: File) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CourseLocalDataSource(courseDao, userActionDao, file).also { INSTANCE = it }
            }
    }


    override fun saveCourse(course: Course): Observable<Boolean> {
        return Observable.fromCallable {
            courseDao.insert(course)
            true
        }
    }

    override fun saveUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.insert(userAction)
            true
        }
    }

    override fun deleteCourse(course: Course): Observable<Boolean> {
        return Observable.fromCallable {
            courseDao.delete(course)
            true
        }
    }

    override fun deleteCourseList(courseList: List<Course>): Observable<Boolean> {
        return Observable.fromCallable {
            courseDao.delete(courseList)
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

    override fun updateCourse(course: Course): Observable<Boolean> {
        return Observable.fromCallable {
            courseDao.update(course)
            true
        }
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            userActionDao.update(userAction)
            true
        }
    }

    override fun getAllCourse(): Flowable<List<Course>> {
        return Flowable.fromCallable {
            courseDao.loadAll()
        }
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            userActionDao.loadAll()
        }
    }

    override fun getCourseForKeyword(keyword: String): Flowable<List<Course>> {
        return Flowable.fromCallable {
            courseDao.searchCourseForKeyword(keyword)
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

    override fun saveJsonFile(fileName: String, jsonObj: JSONObject) {
        NewFileUtils.saveJsonFile(file, fileName, jsonObj)
    }

    override fun loadCoordinateListFromJsonFile(fileName: String): Flowable<List<TimeCode>> {
        return Flowable.fromCallable { NewFileUtils.loadCoordinateListFromJsonFile(file, fileName) }
    }

    override fun deleteCourseFile(fileName: String) {
        NewFileUtils.deleteCourseFile(file, fileName)
    }
}
