package com.boostcamp.travery.mapservice

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
import android.os.Looper
import com.boostcamp.travery.main.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.tedpark.tedpermission.rx2.TedRx2Permission


@SuppressLint("Registered")
class MapTrackingService : Service(), MapTrackingContract.Model {
    private val mFusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationRequest: LocationRequest by lazy {
        LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }

    private val UPDATE_INTERVAL_MS: Long = 2500  // 1초
    private val FASTEST_UPDATE_INTERVAL_MS: Long = 1500 //

    private val locationList: ArrayList<LatLng> = ArrayList()
    private val TAG = "MyLocationService"

    private var exLocation: Location? = null
    private var totalDistance = 0f
    var isRunning = false
    private var second: Int = 0
    private var countThread: Thread? = null
    private var mCallback: ICallback? = null
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

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val nowLocationList = locationResult.locations
            if (nowLocationList.size > 0) {
                val location = nowLocationList.last()
                //location = locationList.get(0);
                //currentPosition = LatLng(location.getLatitude(), location.getLongitude())
                Log.d(TAG, "onLocationResult : " + LatLng(location.latitude, location.longitude))

                if (exLocation != null) {
                    val dis = location.distanceTo(exLocation)
                    if (dis >= 1 && dis < 7) {
                        totalDistance += location.distanceTo(exLocation)
                        val locate = LatLng(location.latitude, location.longitude)
                        locationList.add(locate)
                        mCallback?.sendData(locate)
                    }
                    exLocation = location

                    Log.d(TAG, "onLocationResult: ${totalDistance}")
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

        TedRx2Permission.with(this)
                .setRationaleTitle("Notice")
                .setRationaleMessage("we need permission for find your location") // "we need permission for read contact and find your location"
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .request()
                .subscribe({ tedPermissionResult ->
                    if (tedPermissionResult.isGranted) {
                        try {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

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

        isRunning = true

        if (countThread == null) {
            countThread = Thread(Counter())
            countThread!!.start()
        }
        //do heavy work on a background thread
        //stopSelf();

        return Service.START_NOT_STICKY
    }

    private fun getLastKnownLocation(): Location? {
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        if (isRunning && bestLocation != null) {
            locationList.add(LatLng(bestLocation.latitude, bestLocation.longitude))
        }
        return bestLocation
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        countThread = null
        isRunning = false
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        }

    }

    inner class Counter : Runnable {

        override fun run() {
            second = 0
            while (true) {
                if (!isRunning) {
                    break
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                second++
            }
        }
    }

    interface ICallback {
        fun sendData(location: LatLng)
    }

    fun registerCallback(cb: ICallback) {
        mCallback = cb
    }

    override fun getTotalSecond(): Int {
        return second
    }

    override fun getFinishData(): Route {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastLocation(): Location? {

        return getLastKnownLocation()
    }

    override fun getLocationList(): ArrayList<LatLng> {
        return locationList
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}