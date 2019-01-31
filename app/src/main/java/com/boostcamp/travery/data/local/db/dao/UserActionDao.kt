package com.boostcamp.travery.data.local.db.dao

import androidx.room.*
import com.boostcamp.travery.data.model.UserAction

@Dao
interface UserActionDao {
    @Insert
    fun insert(userAction: UserAction)

    @Insert
    fun insert(userActionList: List<UserAction>)

    @Update
    fun update(userAction: UserAction)

    @Delete
    fun delete(userAction: UserAction)

    @Delete
    fun delete(userActionList: List<UserAction>)

    @Query("SELECT * FROM useraction")
    fun loadAll(): List<UserAction>

    /**
     * 해당 경로에 속하는 활동들을 반환
     *
     * @param courseKey 경로 고유 키 값
     *
     * @return 활동 목록
     */
    @Query("SELECT * FROM useraction WHERE course_code=:courseKey")
    fun loadUserActionForCourse(courseKey: Long): List<UserAction>

    /**
     * 해당 keyword를 title,body,hashtag에 포함한 활동들을 반환
     *
     * @param courseKey 경로 고유 키 값
     *
     * @return 활동 목록
     */
    @Query("SELECT * FROM useraction WHERE title LIKE '%'||:keyword||'%' OR body LIKE '%'||:keyword||'%' OR hashtag LIKE '%'||:keyword||'%'")
    fun searchUserActionForKeyword(keyword: String): List<UserAction>
}