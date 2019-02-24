package com.boostcamp.travery.data.repository

import com.boostcamp.travery.data.UserDataSource
import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.UserRemoteDataSource
import com.boostcamp.travery.data.remote.model.MyResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class UserRepository private constructor(private val userDataSource: UserDataSource) :
    UserDataSource {

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        @JvmStatic
        fun getInstance() = INSTANCE
            ?: synchronized(this) {
                INSTANCE
                    ?: UserRepository(UserRemoteDataSource.getInstance()).also {
                        INSTANCE = it
                    }
            }
    }

    override fun checkNickname(nickname: String): Single<Boolean> {
        return userDataSource.checkNickname(nickname).observeOn(AndroidSchedulers.mainThread())
    }

    override fun registeredUserId(userId: String): Single<User> {
        return userDataSource.registeredUserId(userId).observeOn(AndroidSchedulers.mainThread())
    }

    override fun registerUser(user: User): Single<MyResponse> {
        return userDataSource.registerUser(user).observeOn(AndroidSchedulers.mainThread())
    }
}