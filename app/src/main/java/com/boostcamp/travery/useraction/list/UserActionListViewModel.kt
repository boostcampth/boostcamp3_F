package com.boostcamp.travery.useraction.list

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.MyApplication
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.local.db.AppDatabase
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.data.repository.UserActionRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserActionListViewModel(application: Application) : BaseViewModel(application) {
    private var contract: Contract? = null
    private val curLocation = MutableLiveData<Location>()

    val userActionList = ObservableArrayList<UserAction>()

    private val userActionRepository =
            UserActionRepository.getInstance(AppDatabase.getInstance(application).daoUserAction())

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
        addDisposable(
                userActionRepository.getAllUserAction()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            contract?.onUserActionLoading(it)

                            userActionList.clear()
                            userActionList.addAll(it)
                        })
    }

    fun setCurrentLocation() {
        getLastKnownLocation()?.let {
            curLocation.value = it
        }
    }

    private fun getLastKnownLocation(): Location? {
        val fineLocPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val courseLocPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        val locationManager = getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        val isGranted = PackageManager.PERMISSION_GRANTED
        if (fineLocPerm == isGranted && courseLocPerm == isGranted) {
            for (provider in providers) {
                val mLocation = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || mLocation.accuracy < bestLocation.accuracy) {
                    bestLocation = mLocation
                }
            }
        }

        return bestLocation
    }

    private fun getContext() = getApplication<MyApplication>()
}