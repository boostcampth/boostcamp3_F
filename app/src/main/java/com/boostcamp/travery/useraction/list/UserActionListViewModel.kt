package com.boostcamp.travery.useraction.list

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.MyApplication
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserActionListViewModel(application: Application) : BaseViewModel(application) {
    private var contract: Contract? = null
    private val curLocation = MutableLiveData<Location>()

    fun getCurLocation() = curLocation

    init {
        loadUserActions()
    }

    interface Contract {
        fun onUserActionLoading(list: List<UserAction>)
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    private fun loadUserActions() {
        repository.getAllUserAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    contract?.onUserActionLoading(it)
                }.also { addDisposable(it) }
    }

    fun setCurrentLocation() {
        val fineLocPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val courseLocPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        curLocation.value = PackageManager.PERMISSION_GRANTED.let { isGranted ->
            if (fineLocPerm == isGranted && courseLocPerm == isGranted) {
                (getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else {
                null
            }
        }
    }

    private fun getContext() = getApplication<MyApplication>()
}