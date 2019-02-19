package com.boostcamp.travery.data

import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.model.MyResponse
import io.reactivex.Single

interface UserDataSource {

    fun checkNickname(nickname: String): Single<Boolean>

    fun registeredUserId(userId: String): Single<User>

    fun registerUser(user:User):Single<MyResponse>
}