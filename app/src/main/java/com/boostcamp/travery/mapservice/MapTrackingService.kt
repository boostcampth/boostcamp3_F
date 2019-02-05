package com.boostcamp.travery.mapservice

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat

import android.location.LocationManager
import android.util.Log
import android.content.Context
import com.boostcamp.travery.main.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.boostcamp.travery.R
import java.util.*
import android.os.*
import com.boostcamp.travery.data.model.TimeCode
import kotlin.collections.ArrayList


@SuppressLint("Registered")
class MapTrackingService : Service() {

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
                this
        )
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }

    private val UPDATE_INTERVAL_MS: Long = 2500  // 1초
    private val FASTEST_UPDATE_INTERVAL_MS: Long = 1500 //
    private var lostLocationCnt = 0

    private val timeCodeList: ArrayList<TimeCode> = ArrayList()
    private var canSuggest = true
    private val suggestList: ArrayList<LatLng> = ArrayList()
    private val TAG = "MyLocationService"

    private var startTime: Long? = null
    private var exLocation: Location? = null
    private var totalDistance = 0f
    var isRunning = false
    private var second: Int = 0
    private var mCallback: ICallback? = null
    private val mLocationManager: LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val notification: NotificationCompat.Builder by lazy {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_message))
                .setSmallIcon(R.drawable.ic_play_circle_filled_black_60dp)
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
                //location = locationList.get(0);
                //currentPosition = LatLng(location.getLatitude(), location.getLongitude())
                Log.d(TAG, "onLocationResult : " + location.accuracy)

                if (exLocation != null) {
                    val dis = location.distanceTo(exLocation)
                    //이동거리가 1m 이상 10m 이하이고 오차범위가 10m 미만일 때
                    //실내에서는 12m~30m정도의 오차 발생
                    //야외에서는 3m~11m정도의 오차 발생
                    if (dis >= 1/* && dis < 10 && location.accuracy < 9.5*/) {
                        totalDistance += location.distanceTo(exLocation)
                        val locate = LatLng(location.latitude, location.longitude)
                        timeCodeList.add(TimeCode(locate, location.time))
                        mCallback?.sendLocation(locate, location.accuracy)
                        exLocation = location

                        if (lostLocationCnt > 60 && canSuggest) {
                            suggestList.add(locate)
                        }
                        canSuggest = true
                        lostLocationCnt = 0
                    } else {
                        lostLocationCnt++
                    }

                    //Log.d(TAG, "onLocationResult: ${totalDistance}")
                } else {
                    exLocation = location
                }
                //Log.d(TAG, "onLocationChanged: ${location.time}")

                //시간
                /*val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.KOREA)
                val formatted = format.format(location.time)
                Log.d(TAG, "onLocationChanged: $formatted")*/

                //mLastLocation.set(location)]
                //if (mCallback != null) {

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
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground(1, notification.build())

        isRunning = true

        secondTimer.schedule(SecondTimer(), 1000, 1000)

        startTime = System.currentTimeMillis()
        mCallback?.saveInitCourse(startTime?:System.currentTimeMillis())

        return Service.START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val mLocation = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || mLocation.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = mLocation
            }
        }
        if (isRunning && bestLocation != null) {
            timeCodeList.add(TimeCode(LatLng(bestLocation.latitude, bestLocation.longitude),bestLocation.time))
        }
        return bestLocation
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        isRunning = false
        secondTimer.cancel()
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    inner class SecondTimer : TimerTask() {
        override fun run() {
            second++
            mCallback?.sendSecond(second)
        }
    }

    interface ICallback {
        fun sendLocation(location: LatLng, accuracy: Float)
        fun sendSecond(second: Int)
        fun saveInitCourse(startTime: Long)
    }

    fun registerCallback(cb: ICallback) {
        mCallback = cb
    }

    fun getStartTime(): Long {
        return startTime ?: 0
    }

    fun getEndTime(): Long {
        return (startTime ?: 0+second.toLong())
    }

    fun getTotalDistance(): Long {
        return totalDistance.toLong()
    }

    fun getLastLocation(): Location? {

        return getLastKnownLocation()
    }

    fun getTimeCodeList(): ArrayList<TimeCode> {
        return timeCodeList
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}