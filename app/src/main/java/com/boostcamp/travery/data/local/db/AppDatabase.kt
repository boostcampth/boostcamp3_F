package com.boostcamp.travery.data.local.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.boostcamp.travery.data.local.db.dao.CourseDao
import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction

/**
 * room database course 테이블과 useraction 테이블을 가짐.
 */
@Database(entities = [Course::class, UserAction::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "travery")
                    .build()
            }
            return INSTANCE as AppDatabase
        }
    }

    abstract fun daoUserAction(): UserActionDao
    abstract fun daoCourse(): CourseDao
}