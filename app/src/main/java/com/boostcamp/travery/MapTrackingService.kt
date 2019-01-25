package com.boostcamp.travery

import android.Manifest
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
import android.support.v4.app.ActivityCompat
import com.boostcamp.travery.MyApplication.Companion.CHANNEL_ID
import com.boostcamp.travery.data.model.Route
import android.widget.Toast
import android.R
import android.location.LocationListener
import com.tedpark.tedpermission.rx2.TedRx2Permission

@SuppressLint("Registered")
class MapTrackingService : Service(), MapTrackingContract.Model {
    private val TAG = "MyLocationService"
    private val LOCATION_INTERVAL: Long = 1000
    private val LOCATION_DISTANCE = 1f
    private var exLocation: Location? = null
    private var totalDistance = 0f
    var isRunning = false
    private var count: Int = 0
    private var countThread: Thread? = null
    private var mCallback:ICallback ?= null
    private val mLocationManager: LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val notification: NotificationCompat.Builder by lazy {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("활동 기록 중 입니다.")
                .setSmallIcon(R.drawable.btn_star)
                .setContentIntent(pendingIntent)
    }
    private val mBinder = LocalBinder()

    internal inner class LocalBinder : Binder() {
        val service: MapTrackingService
            get() = this@MapTrackingService
    }

    private val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged: $location")
            if (exLocation != null) {
                totalDistance += location.distanceTo(exLocation)
                Log.d(TAG, "onLocationChanged: ${totalDistance}")
            }
            //Log.d(TAG, "onLocationChanged: ${location.time}")

            //시간
            /*val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.KOREA)
            val formatted = format.format(location.time)
            Log.d(TAG, "onLocationChanged: $formatted")*/

            //mLastLocation.set(location)
            exLocation = location

            //if (mCallback != null) {
            mCallback?.sendData(location)
            //}
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

    @SuppressLint("CheckResult")
    override fun onCreate() {

        Log.e(TAG, "onCreate")

        TedRx2Permission.with(this)
                .setRationaleTitle("dd")
                .setRationaleMessage("dd") // "we need permission for read contact and find your location"
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION)
                .request()
                .subscribe({ tedPermissionResult ->
                    if (tedPermissionResult.isGranted) {
                        //Log.d(TAG, "onLocationChanged: ${getLastKnownLocation()}")
                        try {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    LOCATION_INTERVAL,
                                    LOCATION_DISTANCE,
                                    gpsLocationListener
                            )
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    LOCATION_INTERVAL,
                                    LOCATION_DISTANCE,
                                    gpsLocationListener
                            )
                        } catch (ex: java.lang.SecurityException) {
                            Log.i(TAG, "fail to request location update, ignore", ex)
                        } catch (ex: IllegalArgumentException) {
                            Log.d(TAG, "network provider does not exist, " + ex.message)
                        }
                    } else {
                        Toast.makeText(
                                this,
                                "Permission Denied\n" + tedPermissionResult.getDeniedPermissions().toString(),
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                }, { })
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground(1, notification.build())
        //mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        //val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        //Log.d("lolo", location.toString())

        isRunning = true

        if (countThread == null) {
            countThread = Thread(Counter())
            countThread!!.start()
        }
        //do heavy work on a background thread
        //stopSelf();

        return Service.START_NOT_STICKY
    }

    private fun getLastKnownLocation():Location? {
        val providers = mLocationManager.getProviders(true)
        var bestLocation:Location? = null
        for (provider in providers)
        {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy)
            {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        countThread = null
        isRunning = false
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
            mLocationManager.removeUpdates(gpsLocationListener)
        } catch (ex: Exception) {
            Log.i(TAG, "fail to remove location listener, ignore", ex)
        }

    }

    inner class Counter : Runnable {

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

    interface ICallback {
        fun sendData(location:Location)
    }

    fun registerCallback(cb:ICallback) {
        mCallback = cb
    }

    override fun getTotalSecond(): Int {
        return count
    }

    override fun getFinishData(): Route {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastLocation(): Location? {

        return getLastKnownLocation()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}