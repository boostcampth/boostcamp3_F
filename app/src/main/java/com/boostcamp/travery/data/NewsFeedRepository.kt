package com.boostcamp.travery.data;

import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.remote.NewsFeedRemoteDataSource
import com.boostcamp.travery.data.remote.model.MyResponse
import com.boostcamp.travery.data.remote.model.NewsFeed
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class NewsFeedRepository private constructor(private val newsFeedDataSource: NewsFeedDataSource) : NewsFeedDataSource {

    companion object {
        @Volatile
        private var INSTANCE: NewsFeedRepository? = null

        @JvmStatic
        fun getInstance() = INSTANCE
            ?: synchronized(this) {
                INSTANCE
                    ?: NewsFeedRepository(NewsFeedRemoteDataSource.getInstance()).also {
                        INSTANCE = it
                    }
            }
    }


    override fun getFeedList(start: Int): Single<List<NewsFeed>> {
        return newsFeedDataSource.getFeedList(start).observeOn(AndroidSchedulers.mainThread())
    }

    override fun uploadFeed(userAction: UserAction, userId: String): Single<MyResponse> {
        return newsFeedDataSource.uploadFeed(userAction,userId).observeOn(AndroidSchedulers.mainThread())
    }
}
