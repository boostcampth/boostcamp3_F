package com.boostcamp.travery.data.remote

import com.boostcamp.travery.data.UserDataSource
import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.model.MyResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

class UserRemoteDataSource : UserDataSource {

    companion object {
        @Volatile
        private var INSTANCE: UserRemoteDataSource? = null

        @JvmStatic
        fun getInstance() =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: UserRemoteDataSource().also { INSTANCE = it }
                }
    }

    override fun checkNickname(nickname: String): Single<Boolean> {
        return ApiService.getInstance()
                .api
                .checkNickname(nickname)
                .subscribeOn(Schedulers.io())
    }

    override fun registeredUserId(userId: String): Single<User> {
        return ApiService.getInstance()
                .api
                .registeredUserId(userId)
                .subscribeOn(Schedulers.io())
    }

    override fun registerUser(user: User): Single<MyResponse> {
        val data = HashMap<String, Any>()
        data["id"] = user.id
        data["nickName"] = user.nickname

        var body:MultipartBody.Part?=null
        val item = RequestBody.create(MediaType.parse("text/plain"), JSONObject(data).toString())
        if(!user.image.isNullOrEmpty()){
            val file = File(user.image)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        }
        return ApiService.getInstance()
                .api
                .postUserInfo(item, body)
                .subscribeOn(Schedulers.io())
    }
}