package com.boostcamp.travery.data

import com.boostcamp.travery.data.local.db.DbHelper
import com.boostcamp.travery.data.local.prefs.PreferHelper
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.data.remote.ApiHelper
import io.reactivex.Flowable

interface DataManager : DbHelper, ApiHelper, PreferHelper {
    fun insertDummyData()

}