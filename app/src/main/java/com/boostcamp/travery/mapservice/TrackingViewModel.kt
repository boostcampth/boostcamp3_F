package com.boostcamp.travery.mapservice

import android.app.Application
import android.os.Bundle
import android.view.View
import com.boostcamp.travery.base.BaseViewModel
import android.widget.AdapterView
import androidx.databinding.ObservableField
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.TimeCode
import com.boostcamp.travery.utils.FileUtils
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder


class TrackingViewModel(application: Application) : BaseViewModel(application) {

    val isService = ObservableField<Boolean>(false)
    val isBound = ObservableField<Boolean>(false)

    fun startService(v :View){
        isService.set(true)
    }

    fun stopService(v :View){
        isService.set(true)
    }
}