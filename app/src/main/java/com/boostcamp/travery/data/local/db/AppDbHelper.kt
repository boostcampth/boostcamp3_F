package com.boostcamp.travery.data.local.db

import android.content.Context
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable

class AppDbHelper private constructor(private var appDatabase: AppDatabase) : DbHelper {

    companion object {
        private var appDbHelper: AppDbHelper? = null
        fun getInstance(context: Context): AppDbHelper {
            if (appDbHelper == null) {
                appDbHelper = AppDbHelper(AppDatabase.getInstance(context))
            }
            return appDbHelper as AppDbHelper
        }
    }

    override fun saveCourse(course: Course): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoCourse().insert(course)
            true
        }
    }

    override fun saveUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoUserAction().insert(userAction)
            true
        }
    }

    override fun saveUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoUserAction().insert(userActionList)
            true
        }
    }

    override fun deleteCourse(course: Course): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoCourse().delete(course)
            true
        }
    }

    override fun deleteCourseList(courseList: List<Course>): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoCourse().delete(courseList)
            true
        }
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoUserAction().delete(userAction)
            true
        }
    }

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoUserAction().delete(userActionList)
            true
        }
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        return Observable.fromCallable {
            appDatabase.daoUserAction().update(userAction)
            true
        }
    }

    override fun getAllCourse(): Flowable<List<Course>> {
        return Flowable.fromCallable {
            appDatabase.daoCourse().loadAll()
        }
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            appDatabase.daoUserAction().loadAll()
        }
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            appDatabase.daoUserAction().loadUserActionForCourse(course.startTime)
        }
    }

    override fun getCourseForKeyword(keyword: String): Flowable<List<Course>> {
        return Flowable.fromCallable {
            appDatabase.daoCourse().searchCourseForKeyword(keyword)
        }
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        return Flowable.fromCallable {
            appDatabase.daoUserAction().searchUserActionForKeyword(keyword)
        }
    }
}