package com.boostcamp.travery.data.remote

import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.model.MyResponse
import com.boostcamp.travery.data.remote.model.NewsFeed
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class ApiService {

    val api: Api

    companion object {
        const val API_URL = "http://35.229.76.14/"

        @Volatile
        private var INSTANCE: ApiService? = null

        @JvmStatic
        fun getInstance() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ApiService().also { INSTANCE = it }
        }
    }

    init {
        val stethoInterceptingClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(stethoInterceptingClient)
                .build()

        api = retrofit.create(Api::class.java)
    }


    interface Api {
        @Multipart
        @POST("write.php")
        fun uploadImage(@Part("item") data: RequestBody, @Part files: List<MultipartBody.Part>): Single<Response<MyResponse>>

        /**
         * 게시판 목록을 불러오는 메소드
         */
        @GET("boardList.php")
        fun getBoardList(@Query("start") start: Int): Single<Response<List<NewsFeed>>>

        /**
         * @return true : 사용 가능
         *          false : 사용 불가
         */
        @GET("userValidate.php")
        fun checkNickname(@Query("userNick") nickname:String):Single<Boolean>

        /**
         *
         * @return 사용자 있다면 값반환 없다면 null
         */
        @FormUrlEncoded
        @POST("userValidate.php")
        fun registeredUserId(@Field("userId") userId:String):Single<User>

        @Multipart
        @POST("join.php")
        fun postUserInfo(@Part("user")user:RequestBody,@Part file:MultipartBody.Part?):Single<MyResponse>
    }
}