package com.boostcamp.travery.mapservice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import android.os.Handler
import kotlinx.android.synthetic.main.activity_tracking.*
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.graphics.Color
import android.os.Looper
import android.util.Log
import com.boostcamp.travery.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.tedpark.tedpermission.rx2.TedRx2Permission


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {
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

    private val UPDATE_INTERVAL_MS: Long = 2500
    private val FASTEST_UPDATE_INTERVAL_MS: Long = 1500

    lateinit var myService: MapTrackingService
    var isService = false
    private lateinit var mMap: GoogleMap
    private var myLocationMarker: Marker? = null
    private var polyline: Polyline? = null
    private var lastLocation = LatLng(37.56, 126.97)
    private val polylineOptions: PolylineOptions = PolylineOptions()

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val nowLocationList = locationResult.locations
            if (nowLocationList.size > 0) {
                val location = nowLocationList.last()
                //location = locationList.get(0);
                //currentPosition = LatLng(location.getLatitude(), location.getLongitude())
                Log.d("TrackingActivity", "onLocationResult : " + LatLng(location.latitude, location.longitude))

                val locate = LatLng(location.latitude, location.longitude)
                myLocationMarker?.position = locate
                lastLocation = locate
                Log.d("TrackingActivity", "onLocationResult: ${locate}")

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        polylineOptions.color(Color.BLUE)
                //.geodesic(true)
                .width(10f)

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGPS()
    }

    fun startService(v: View) {
        stopGPS()
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
        tv_text.visibility = View.VISIBLE
    }

    fun stopService(v: View) {
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        unbindService(conn)
        stopService(serviceIntent)
        btn_stop.visibility = View.INVISIBLE
        img_midMarker.visibility = View.VISIBLE
        btn_play.visibility = View.VISIBLE
        tv_text.visibility = View.GONE
        startGPS()
    }

    fun gotoMyLocation(v: View) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLocation))
    }

    private var conn: ServiceConnection = object : ServiceConnection {

        private val mCallback = object : MapTrackingService.ICallback {
            override fun sendData(location: LatLng, accuracy: Float) {
                myLocationMarker?.position = location
                //arrayPoints.add(locate)
                tv_acc.text = accuracy.toString()
                polylineOptions.add(location)
                polyline?.remove()
                polyline = mMap.addPolyline(polylineOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(location))
            }
            /* 서비스에서 데이터를 받아 메소드 호출 또는 핸들러로 전달 */
        }

        override fun onServiceConnected(
                name: ComponentName,
                service: IBinder
        ) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            val mb = service as MapTrackingService.LocalBinder
            myService = mb.service // 서비스가 제공하는 메소드 호출하여
            myService.registerCallback(mCallback)
            // 서비스쪽 객체를 전달받을수 있슴
            isService = myService.isRunning

            if (myLocationMarker == null) {
                val location = myService.getLastLocation()
                //서울 위치
                var lat = 37.56
                var lng = 126.97
                if (location != null) {
                    lat = location.latitude
                    lng = location.longitude
                }

                val myLocation = LatLng(lat, lng)
                myLocationMarker = mMap.addMarker(MarkerOptions().position(myLocation))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }

            //서비스가 돌고 있을 때
            if (isService) {
                val counter = Thread(Counter())
                counter.start()
                polylineOptions.addAll(myService.getLocationList())
                polyline = mMap.addPolyline(polylineOptions)


                btn_stop.visibility = View.VISIBLE
                img_midMarker.visibility = View.INVISIBLE
                btn_play.visibility = View.INVISIBLE
            } else {//서비스는 돌지 않고 바인드만 했을 때 바인드를 끊는다.
                startGPS()
                unbindService(this)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false
        }
    }

    inner class Counter : Runnable {

        private val handler = Handler()
        override fun run() {
            while (true) {
                if (!isService) {
                    break
                }
                handler.post {
                    tv_text.text = setIntToTime(myService.getTotalSecond())
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setIntToTime(timeInt: Int): String {

        var min = timeInt / 60
        val hour = min / 60
        val sec = timeInt % 60
        min %= 60

        return "${String.format("%02d", hour)}:${String.format("%02d", min)}:${String.format("%02d", sec)}"
    }

    @SuppressLint("CheckResult")
    fun startGPS() {

        try {
            mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
            )

        } catch (ex: java.lang.SecurityException) {
            //Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            //Log.d(TAG, "network provider does not exist, " + ex.message)
        }
    }

    private fun stopGPS() {

        mFusedLocationClient.removeLocationUpdates(locationCallback)

    }
}
