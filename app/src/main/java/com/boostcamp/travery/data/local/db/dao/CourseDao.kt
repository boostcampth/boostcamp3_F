package com.boostcamp.travery.data.local.db.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.boostcamp.travery.data.model.Course

@Dao
interface CourseDao {
    @Insert(onConflict = IGNORE)
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)

    @Delete
    fun delete(courseList: List<Course>)

    @Update
    fun update(course: Course)

    /**
     * 저장된 모든 경로 리스트 반환
     */
    @Query("SELECT * FROM course ORDER BY start_time DESC")
    fun loadAll(): List<Course>

    /**
     * title 또는 theme 에 해당 키워드가 포함된 경로검색
     *
     * @param keyword 검색단어
     * @param index 시작 인덱스4
     * @param count 가져올 course 갯수
     *
     * @return 해당 키워드로 검색된 row 중 지정 인덱스부터 count 만큼  반환
     */
    @Query("SELECT * FROM course WHERE title LIKE '%'||:keyword||'%' OR theme LIKE '%'||:keyword||'%'")
    fun searchCourseForKeyword(keyword: String): List<Course>

    @Query("SELECT * FROM course  WHERE start_time >=:today ")
    fun loadTodayCourse(today: Long): List<Course>
}