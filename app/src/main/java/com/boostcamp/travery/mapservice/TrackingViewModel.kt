package com.boostcamp.travery.mapservice

import android.app.Application
import com.boostcamp.travery.base.BaseViewModel
import androidx.databinding.ObservableField


class TrackingViewModel(application: Application) : BaseViewModel(application) {

    val isService = ObservableField<Boolean>(false)

    fun getIsServiceState(): Boolean {
        return isService.get() ?: false
    }

    fun setIsServiceState(boolean: Boolean) {
        isService.set(boolean)
    }
}