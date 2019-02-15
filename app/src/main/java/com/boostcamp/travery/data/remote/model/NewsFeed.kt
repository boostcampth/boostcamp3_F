package com.boostcamp.travery.data.remote.model

import com.boostcamp.travery.data.model.User
import java.util.*

data class NewsFeed(
    var id:Int,
    var title:String,
    var body:String,
    var date:Date,
    var hashTag:String,
    var image:String,
    var latitude:Double,
    var longitude:Double,
    var user: User
)