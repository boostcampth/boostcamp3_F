package com.boostcamp.travery

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.support.v4.app.NotificationCompat


import android.os.Bundle
import android.location.LocationManager
import android.util.Log
import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Handler
import android.support.v4.app.ActivityCompat
import com.boostcamp.travery.MyApplication.Companion.CHANNEL_ID


//GPS 받아오는 서비스 코틀린에 맞는 코드 수정 필요
@SuppressLint("Registered")
class MapTrackingService : Service(), MapTrackingContract.Model {
    private val TAG = "MyLocationService"
    private var mLocationManager: LocationManager? = null
    private val LOCATION_INTERVAL: Long = 2000
    private val LOCATION_DISTANCE = 1f
    private var exLocation: Location? = null
    private var totalDistance = 0f
    var isRunning = false
    private var count: Int = 0
    private var countThread: Thread? = null
    private val notification: NotificationCompat.Builder by lazy {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("활동 기록 중 입니다.")
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentIntent(pendingIntent)
    }
    private val mBinder = LocalBinder()

    internal inner class LocalBinder : Binder() {
        val service: MapTrackingService
            get() = this@MapTrackingService
    }

    inner class LocationListener(provider: String) : android.location.LocationListener {
        private var mLastLocation: Location

        init {
            Log.d(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged: $location")
            if (exLocation != null) {
                totalDistance += location.distanceTo(exLocation)
                Log.d(TAG, "onLocationChanged: ${totalDistance}")
            }
            Log.d(TAG, "onLocationChanged: ${location.time}")
            mLastLocation.set(location)
            exLocation = location
        }

        override fun onProviderDisabled(provider: String) {
            Log.d(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.d(TAG, "onStatusChanged: $provider")
        }
    }

    var mLocationListeners = arrayOf(LocationListener(LocationManager.PASSIVE_PROVIDER))

    override fun onCreate() {

        Log.e(TAG, "onCreate")

        initializeLocationManager()

        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            )
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground(1, notification.build())

        isRunning = true

        if (countThread == null) {
            countThread = Thread(Counter())
            countThread!!.start()
        }
        //do heavy work on a background thread
        //stopSelf();

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        countThread = null
        isRunning = false
        if (mLocationManager != null) {
            for (i in 0 until mLocationListeners.size) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex)
                }

            }
        }
    }

    private fun initializeLocationManager() {
        Log.e(
                TAG,
                "initializeLocationManager - LOCATION_INTERVAL: $LOCATION_INTERVAL LOCATION_DISTANCE: $LOCATION_DISTANCE"
        )
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    inner class Counter : Runnable {

        private val handler = Handler()
        override fun run() {
            count = 0
            while (true) {
                if (!isRunning) {
                    break
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                count++
            }
        }
    }

    override fun getTotalSecond(): Int {
        return count
    }

    override fun getFinishData(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}