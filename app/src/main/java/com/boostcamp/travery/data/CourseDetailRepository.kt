package com.boostcamp.travery.data

import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.local.source.CourseDetailLocalDataSource
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.utils.FileUtils
import io.reactivex.Flowable
import java.io.File

class CourseDetailRepository private constructor(userActionDao: UserActionDao) : CourseDetailDataSource {

    private val localDataSource by lazy { CourseDetailLocalDataSource.getInstance(userActionDao) }

    private var mCachedCourseId:Long?=null
    private var mCachedUserActionList: ArrayList<UserAction>? = null
    private var mCachedTimeCodeList:List<TimeCode>?=null
    private var mCacheIsDirty = false

    companion object {

        private var INSTANCE: CourseDetailRepository? = null

        @JvmStatic
        fun getInstance(userActionDao: UserActionDao) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CourseDetailRepository(userActionDao).also { INSTANCE = it }
        }
    }


    override fun getUserActionForCourse(course: Course): Flowable<List<UserAction>> {


        if (mCachedCourseId!=null&&mCachedCourseId==course.startTime&&!mCacheIsDirty) {
            return Flowable.fromArray(mCachedUserActionList)
        } else if(mCachedCourseId==null) {
            mCachedCourseId=course.startTime
            mCachedUserActionList = ArrayList()
        }

        return localDataSource.getUserActionForCourse(course).doOnNext { updateCachedData(course.startTime,it) }
    }




    private fun updateCachedData(courseId:Long,userActionList: List<UserAction>) {
        mCachedCourseId=courseId
        mCachedUserActionList?.clear()
        mCachedUserActionList?.addAll(userActionList)

    }

     fun loadCourseCoordinate(file: File, fileName: String): Flowable<List<TimeCode>> {
         return if(fileName == mCachedCourseId?.toString()){
             Flowable.fromArray(mCachedTimeCodeList)
         }else{
             Flowable.fromCallable {
                 FileUtils.loadCoordinateListFromJsonFile(file, fileName)
             }.doOnNext { mCachedTimeCodeList=it }
         }
    }

}