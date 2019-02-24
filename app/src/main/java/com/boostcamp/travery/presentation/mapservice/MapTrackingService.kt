package com.boostcamp.travery.presentation.mapservice

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat

import android.location.LocationManager
import android.util.Log
import android.content.Context
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.boostcamp.travery.R
import java.util.*
import android.os.*
import androidx.core.app.TaskStackBuilder
import com.boostcamp.travery.Constants
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.data.repository.MapTrackingRepository

class MapTrackingService : Service() {

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
                this
        )
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.UPDATE_INTERVAL_MS)
                .setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_MS)
    }
    private val TAG = "MyLocationService"


    private var lostLocationCnt = 0
    var isRunning = false
    private var canSuggest = true
    private var second: Int = 0

    private var exLocation: Location? = null
    private lateinit var standardLocation: Location

    private val mapTrackingRepository = MapTrackingRepository.getInstance()

    private val mLocationManager: LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val notification: NotificationCompat.Builder by lazy {
        val notificationIntent = Intent(this, TrackingActivity::class.java)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentText(getString(R.string.service_message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
    }
    private val mBinder = LocalBinder()

    lateinit var secondTimer: Timer

    internal inner class LocalBinder : Binder() {
        val service: MapTrackingService
            get() = this@MapTrackingService
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val nowLocationList = locationResult.locations
            if (nowLocationList.size > 0) {
                val location = nowLocationList.last()
                Log.d(TAG, "onLocationResult : " + location.accuracy)

                if (exLocation != null) {
                    //이동거리가 1m 이상 10m 이하이고 오차범위가 10m 미만일 때
                    //실내에서는 12m~30m정도의 오차 발생
                    //야외에서는 3m~11m정도의 오차 발생
                    if (location.distanceTo(exLocation) >= 2 && location.accuracy < 9.5) {

                        mapTrackingRepository.addTotalDistance(location.distanceTo(exLocation))
                        mapTrackingRepository.addTimeCode(location)

                        exLocation = location

                        //이동 거리가 11m이하에서 움직일 때에는 lostLocationCnt 상승
                        if (location.distanceTo(standardLocation) < 11) {
                            if (isRunning) lostLocationCnt++
                        } else {//1.5초에서 2.5초에 한번 씩 데이터가 들어옴
                            //200번 쌓이면 5분
                            if (lostLocationCnt > 200 && canSuggest) {
                                mapTrackingRepository.addSuggest(Suggestion(
                                        LatLng(
                                                standardLocation.latitude,
                                                standardLocation.longitude
                                        ), standardLocation.time, location.time
                                ))
                            }
                            standardLocation = location
                            canSuggest = true
                            lostLocationCnt = 0
                        }
                    } else {
                        if (isRunning)
                            lostLocationCnt++
                        else {
                            exLocation = location
                            standardLocation = location
                            mapTrackingRepository.addTimeCode(location)
                        }
                    }

                } else {
                    exLocation = location
                    standardLocation = location
                    mapTrackingRepository.addTimeCode(location)
                }

            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate() {

        Log.e(TAG, "onCreate")

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        secondTimer = Timer()

        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground(1, notification.build())

        isRunning = true
        mapTrackingRepository.setStartTime(System.currentTimeMillis())
        mapTrackingRepository.setSecond(second)
        secondTimer.schedule(SecondTimer(), 1000, 1000)

        exLocation?.let {
            mapTrackingRepository.addTimeCode(it)
            standardLocation = it
        }

        return Service.START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val mLocation = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || mLocation.accuracy < bestLocation.accuracy) {
                bestLocation = mLocation
            }
        }
        return bestLocation
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        isRunning = false
        secondTimer.cancel()
        mFusedLocationClient.removeLocationUpdates(locationCallback)
        mapTrackingRepository.clearData()
    }

    inner class SecondTimer : TimerTask() {
        override fun run() {
            second++
            mapTrackingRepository.setSecond(second)
        }
    }

    fun setCanSuggestFalse() {
        canSuggest = false
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("mapService", "OnBind")
        getLastKnownLocation()?.let {
            exLocation = it
            standardLocation = it
            mapTrackingRepository.addTimeCode(it)
        }

        return mBinder
    }
}