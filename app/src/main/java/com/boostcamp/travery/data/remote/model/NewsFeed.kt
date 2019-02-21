package com.boostcamp.travery.data.remote.model

import com.boostcamp.travery.Constants
import com.boostcamp.travery.data.model.BaseItem
import com.boostcamp.travery.data.model.User

data class NewsFeed(
        var id: Int,
        var title: String,
        var body: String,
        var date: String,
        var hashTag: String,
        var image: String,
        var latitude: Double,
        var longitude: Double,
        var user: User
) : BaseItem {
    override fun getType(): Int {
        return Constants.TYPE_NEWSFEED
    }
}