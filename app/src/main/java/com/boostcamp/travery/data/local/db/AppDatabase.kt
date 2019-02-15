package com.boostcamp.travery.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.boostcamp.travery.data.local.db.dao.CourseDao
import com.boostcamp.travery.data.local.db.dao.UserActionDao
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.UserAction

/**
 * room database course 테이블과 useraction 테이블을 가짐.
 */
@Database(entities = [Course::class, UserAction::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) = INSTANCE
                ?: synchronized(this) {
                    INSTANCE ?: (Room.databaseBuilder(context, AppDatabase::class.java, "travery")
                            .addMigrations(MIGRATION_1_2)
                            //.fallbackToDestructiveMigration() // 마이그레션이 안되면 모든 데이터를 삭제해버리는 옵션
                            .build()).also { INSTANCE = it }
                }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE UserAction ADD COLUMN address TEXT DEFAULT ' ' NOT NULL")
            }
        }
    }

    abstract fun daoUserAction(): UserActionDao

    abstract fun daoCourse(): CourseDao
}