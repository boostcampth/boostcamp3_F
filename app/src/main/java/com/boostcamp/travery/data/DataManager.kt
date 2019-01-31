package com.boostcamp.travery.data

import com.boostcamp.travery.data.local.db.DbHelper
import com.boostcamp.travery.data.local.prefs.PreferHelper
import com.boostcamp.travery.data.remote.ApiHelper

interface DataManager : DbHelper, ApiHelper, PreferHelper {

}