package com.boostcamp.travery.data.local

import androidx.room.*
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.Flowable

@Dao
interface UserActionDao {
    @Insert
    fun insertActivity(activity: UserAction)

    @Update
    fun updateActivity(activity: UserAction)

    @Delete
    fun deleteActivity(activity: UserAction)

    /**
     * 해당 경로에 속하는 활동들을 반환
     *
     * @param courseCode 경로 코드
     *
     * @return 활동 목록
     */
    @Query("SELECT * FROM useraction WHERE course_code=:courseCode")
    fun getActivitList(courseCode:Int): Flowable<List<UserAction>>
}