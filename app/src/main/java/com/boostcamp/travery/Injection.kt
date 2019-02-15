package com.boostcamp.travery

import android.content.Context
import com.boostcamp.travery.data.CourseRepository
import com.boostcamp.travery.data.local.db.AppDatabase

class Injection {
    companion object {
        @JvmStatic
        fun provideCourseRepository(context: Context) =
                CourseRepository.getInstance(
                        AppDatabase.getInstance(context).daoCourse(),
                        AppDatabase.getInstance(context).daoUserAction(),
                        context.filesDir
                )
    }
}