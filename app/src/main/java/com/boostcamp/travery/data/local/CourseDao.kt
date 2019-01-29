package com.boostcamp.travery.data.local

import androidx.room.*
import com.boostcamp.travery.data.model.Course
import io.reactivex.Flowable

@Dao
interface CourseDao {
    @Insert
    fun insertCourse(course: Course): Long

    @Update
    fun updateCourse(course: Course)

    @Delete
    fun deleteCourse(course: Course)

    /**
     * 저장된 경로 리스트 반환
     *
     * @param index 시작 인덱스
     * @param count 가져올 course 갯수
     *
     * @return 저장된 경로 중 지정 인덱스부터 count 만큼  반환
     */
    @Query("SELECT * FROM course ORDER BY end_time DESC LIMIT :index,:count ")
    fun loadCourseList(index: Int, count: Int): Flowable<List<Course>>

    /**
     * title 또는 theme 에 해당 키워드가 포함된 경로검색
     *
     * @param keyword 검색단어
     * @param index 시작 인덱스
     * @param count 가져올 course 갯수
     *
     * @return 해당 키워드로 검색된 row 중 지정 인덱스부터 count 만큼  반환
     */
    @Query("SELECT * FROM course WHERE title LIKE '%'||:keyword||'%' OR theme LIKE '%'||:keyword||'%' LIMIT :index,:count")
    fun searchCourseForKeyword(keyword: String, index: Int, count: Int): Flowable<List<Course>>
}