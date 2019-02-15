package com.boostcamp.travery.base

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.boostcamp.travery.MyApplication
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getLastKnownLocation(): Location? {
        val fineLocPerm = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
        val courseLocPerm = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION)

        val locationManager = getApplication<MyApplication>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
}