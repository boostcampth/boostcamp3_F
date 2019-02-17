package com.boostcamp.travery.data.remote

import com.boostcamp.travery.data.NewsFeedDataSource
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.remote.model.MyResponse
import com.boostcamp.travery.data.remote.model.NewsFeed
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class NewsFeedRemoteDataSource : NewsFeedDataSource {

    companion object {
        @Volatile
        private var INSTANCE: NewsFeedRemoteDataSource? = null

        @JvmStatic
        fun getInstance() =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: NewsFeedRemoteDataSource().also { INSTANCE = it }
                }
    }


    override fun getFeedList(start: Int): Single<List<NewsFeed>> {
        return ApiService.getInstance()
                .api
                .getBoardList(start)
                .subscribeOn(Schedulers.io())
                .map { it.body() }
    }

    override fun uploadFeed(userAction: UserAction, userId: String): Single<MyResponse> {
        val data = HashMap<String, Any>()
        data["title"] = userAction.title
        data["body"] = userAction.body
        data["hashTag"] = userAction.hashTag
        data["latitude"] = userAction.latitude
        data["longitude"] = userAction.longitude
        data["userId"] = "temp" //TODO 임시 유저

        val item = RequestBody.create(MediaType.parse("text/plain"), JSONObject(data).toString())

        // 사진 여러개로 확장을 위하여 List로 선언
        val parts = ArrayList<MultipartBody.Part>()

//        if (!userAction.subImage.isEmpty()) {
        val jsonArray = JSONArray(userAction.subImage)
        //추후 여러 장 이미지 일경우 List에 넣는 작업.
        for (i in 0 until jsonArray.length()) {
            val file = File(jsonArray[i] as String)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            parts.add(MultipartBody.Part.createFormData("file[]", file.name, requestFile))
        }

//        }

        return ApiService.getInstance()
                .api
                .uploadImage(item, parts)
                .subscribeOn(Schedulers.io())
                .map { it.body() }
    }

}