package com.boostcamp.travery.data.local

import androidx.room.*
import com.boostcamp.travery.data.model.Route
import io.reactivex.Flowable

@Dao
interface RouteDao {
    @Insert
    fun insertRoute(route: Route): Long

    @Update
    fun updateRoute(route: Route)

    @Delete
    fun deleteRoute(route: Route)

    /**
     * 저장된 경로 리스트 반환
     *
     * @param index 시작 인덱스
     * @param count 가져올 route 갯수
     *
     * @return 저장된 경로 중 지정 인덱스부터 count 만큼  반환
     */
    @Query("SELECT * FROM route ORDER BY end_time DESC LIMIT :index,:count ")
    fun loadRouteList(index: Int, count: Int): Flowable<List<Route>>

    /**
     * title 또는 theme 에 해당 키워드가 포함된 경로검색
     *
     * @param keyword 검색단어
     * @param index 시작 인덱스
     * @param count 가져올 route 갯수
     *
     * @return 해당 키워드로 검색된 row 중 지정 인덱스부터 count 만큼  반환
     */
    @Query("SELECT * FROM route WHERE title LIKE '%'||:keyword||'%' OR theme LIKE '%'||:keyword||'%' LIMIT :index,:count")
    fun searchRouteForKeyword(keyword: String, index: Int, count: Int): Flowable<List<Route>>
}