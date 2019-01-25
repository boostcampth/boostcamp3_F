package com.boostcamp.travery.data.local

import android.arch.persistence.room.*
import com.boostcamp.travery.data.model.Activity
import io.reactivex.Flowable

@Dao
interface ActivityDao {
    @Insert
    fun insertActivity(activity:Activity)

    @Update
    fun updateActivity(activity: Activity)

    @Delete
    fun deleteActivity(activity: Activity)

    /**
     * 해당 경로에 속하는 활동들을 반환
     *
     * @param routeCode 경로 코드
     *
     * @return 활동 목록
     */
    @Query("SELECT * FROM activity WHERE route_code=:routeCode")
    fun getActivitList(routeCode:Int): Flowable<List<Activity>>
}