package com.boostcamp.travery.data

import com.boostcamp.travery.data.local.CourseLocalDataSource
import com.boostcamp.travery.data.local.db.dao.CourseDao
import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.CourseInfo
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable
import io.reactivex.Observable
import org.json.JSONObject
import java.io.File

class CourseRepository private constructor(private val courseDataSource: CourseDataSource) : CourseDataSource {

    private var mCachedCourse = HashMap<Long, CourseInfo>()
    private var mCachedCourseKeyword = HashMap<String, ArrayList<Course>>()
    private var mCacheUserActionKeyword = HashMap<String, ArrayList<UserAction>>()

    companion object {
        @Volatile
        private var INSTANCE: CourseRepository? = null

        @JvmStatic
        fun getInstance(courseDao: CourseDao, userActionDao: UserActionDao, file: File) = INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                            ?: CourseRepository(CourseLocalDataSource.getInstance(courseDao, userActionDao, file)).also {
                                INSTANCE = it
                            }
                }
    }


    override fun saveCourse(course: Course): Observable<Boolean> {
        return courseDataSource.saveCourse(course)
                .doOnComplete {
                    mCachedCourse[course.startTime] = CourseInfo(course, null, null)
                }
    }

    override fun saveUserAction(userAction: UserAction): Observable<Boolean> {
        return courseDataSource.saveUserAction(userAction)
                .doOnComplete {
                    if (userAction.courseCode != null) {
                        //코스가 있는 활동 정보이라면 코스의 userActionList에 넣는다.
                        if (mCachedCourse[userAction.courseCode ?: 0]?.userActionList == null) {
                            mCachedCourse[userAction.courseCode ?: 0]?.userActionList = ArrayList()
                        }
                        mCachedCourse[userAction.courseCode ?: 0]?.userActionList?.add(userAction)
                    }
                }
    }

    override fun deleteCourse(course: Course): Observable<Boolean> {
        return courseDataSource.deleteCourse(course)
                .doOnComplete {
                    mCachedCourse.remove(course.startTime)
                    deleteCourseFile(course.startTime.toString())
                }
    }

    override fun deleteCourseList(courseList: List<Course>): Observable<Boolean> {
        return courseDataSource.deleteCourseList(courseList)
                .doOnComplete {
                    for (course in courseList) {
                        mCachedCourse.remove(course.startTime)
                        deleteCourseFile(course.startTime.toString())
                    }
                }
    }

    override fun deleteUserAction(userAction: UserAction): Observable<Boolean> {
        return courseDataSource.deleteUserAction(userAction)
                .doOnComplete {
                    if (userAction.courseCode != null) {
                        mCachedCourse[userAction.courseCode
                                ?: 0]?.userActionList?.remove(userAction)
                    }
                }
    }

    override fun deleteUserActionList(userActionList: List<UserAction>): Observable<Boolean> {
        return courseDataSource.deleteUserActionList(userActionList)
                .doOnComplete {
                    for (userAction in userActionList) {
                        if (userAction.courseCode != null) {
                            mCachedCourse[userAction.courseCode
                                    ?: 0]?.userActionList?.remove(userAction)
                        }
                    }
                }
    }

    override fun updateCourse(course: Course): Observable<Boolean> {
        return courseDataSource.updateCourse(course)
                .doOnComplete {
                    mCachedCourse[course.startTime]?.course = course
                }
    }

    override fun updateUserAction(userAction: UserAction): Observable<Boolean> {
        return courseDataSource.updateUserAction(userAction)
                .doOnComplete {
                    //경로위에 있는 활동일 때
                    if (userAction.courseCode != null) {
                        mCachedCourse[userAction.courseCode
                                ?: 0]?.userActionList?.remove(userAction)
                        mCachedCourse[userAction.courseCode ?: 0]?.userActionList?.add(userAction)
                    }
                }
    }

    override fun getAllCourse(): Flowable<List<Course>> {
        return courseDataSource.getAllCourse()
    }

    override fun getAllUserAction(): Flowable<List<UserAction>> {
        return courseDataSource.getAllUserAction()
    }

    override fun getCourseForKeyword(keyword: String): Flowable<List<Course>> {
        return if (mCachedCourseKeyword[keyword] != null) {
            Flowable.fromArray(mCachedCourseKeyword[keyword])
        } else {
            courseDataSource.getCourseForKeyword(keyword).doOnNext {
                mCachedCourseKeyword[keyword] = ArrayList(it)
            }
        }
    }

    override fun getUserActionForKeyword(keyword: String): Flowable<List<UserAction>> {
        return if (mCacheUserActionKeyword[keyword] != null) {
            Flowable.fromArray(mCacheUserActionKeyword[keyword])
        } else {
            courseDataSource.getUserActionForKeyword(keyword).doOnNext {
                mCacheUserActionKeyword[keyword] = ArrayList(it)
            }
        }
    }

    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {
        return if (mCachedCourse[course.startTime]?.userActionList != null) {
            Flowable.fromArray(mCachedCourse[course.startTime]?.userActionList)
        } else {
            courseDataSource.getUserActionForCourse(course).doOnNext {
                if (mCachedCourse[course.startTime] == null) {
                    mCachedCourse[course.startTime] = CourseInfo()
                }
                mCachedCourse[course.startTime]?.course = course
                mCachedCourse[course.startTime]?.userActionList = ArrayList(it)
            }
        }
    }

    override fun saveJsonFile(fileName: String, jsonObj: JSONObject) {
        courseDataSource.saveJsonFile(fileName, jsonObj)
    }

    override fun loadCoordinateListFromJsonFile(fileName: String): Flowable<List<TimeCode>> {
        return if (mCachedCourse[fileName.toLong()]?.timeCodeList != null) {
            Flowable.fromArray(mCachedCourse[fileName.toLong()]?.timeCodeList)
        } else {
            courseDataSource.loadCoordinateListFromJsonFile(fileName).doOnNext {
                if (mCachedCourse[fileName.toLong()] == null) {
                    mCachedCourse[fileName.toLong()] = CourseInfo()
                }
                mCachedCourse[fileName.toLong()]?.timeCodeList = ArrayList(it)
            }
        }
    }

    override fun deleteCourseFile(fileName: String) {
        courseDataSource.deleteCourseFile(fileName)
    }
}