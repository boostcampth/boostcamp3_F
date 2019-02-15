package com.boostcamp.travery.data

import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.remote.model.MyResponse
import com.boostcamp.travery.data.remote.model.NewsFeed
import io.reactivex.Single

interface NewsFeedDataSource {

    fun getFeedList(start: Int): Single<List<NewsFeed>>

    fun uploadFeed(userAction: UserAction, userId: String): Single<MyResponse>

}