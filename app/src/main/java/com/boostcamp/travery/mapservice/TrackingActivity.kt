package com.boostcamp.travery.mapservice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.View
import android.content.ComponentName
import android.content.ServiceConnection
import kotlinx.android.synthetic.main.activity_tracking.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.graphics.Color
import android.os.*
import android.util.Log
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.google.android.gms.maps.model.*
import java.lang.ref.WeakReference


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapService: MapTrackingService
    var isService = false
    private lateinit var mMap: GoogleMap
    private lateinit var myLocationMarker: Marker
    private var polyline: Polyline? = null
    private val polylineOptions: PolylineOptions = PolylineOptions()
    private var secondForView = 0
    private val viewHandler = ViewChangeHandler(this)
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        val mapFragment = map_trackingActivity as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        myLocationMarker = mMap.addMarker(MarkerOptions().position(LatLng(37.56, 126.97)))

        polylineOptions.color(Color.BLUE)
            .geodesic(true)
            .width(10f)

        doBindService()
    }

    override fun onDestroy() {
        doUnbindService()
        super.onDestroy()
    }

    fun startService(v: View) {
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        startRecordView()
    }

    fun stopService(v: View) {
        /*
        val EXTRA_ROUTE_END_TIME = "ROUTE_END_TIME"
        val EXTRA_ROUTE_DISTANCE = "ROUTE_DISTANCE"
        val EXTRA_ROUTE_COORDINATE = "ROUTE_COORDINATE"*/
        Log.d("lolot", mapService.getStartTime().toString())
        Log.d("lolot", mapService.getEndTime().toString())
        val saveIntent = Intent(this@TrackingActivity, CourseSaveActivity::class.java)
        saveIntent.putExtra(Constants.EXTRA_ROUTE_START_TIME, mapService.getStartTime())
        saveIntent.putExtra(Constants.EXTRA_ROUTE_END_TIME, mapService.getEndTime())
        saveIntent.putExtra(Constants.EXTRA_ROUTE_DISTANCE, mapService.getTotalDistance())
        saveIntent.putExtra(Constants.EXTRA_ROUTE_COORDINATE, mapService.getStartTime())
        startActivity(saveIntent)

        stopRecordView()
        doUnbindService()
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        stopService(serviceIntent)
    }

    fun gotoMyLocation(v: View) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocationMarker.position))
    }

    private class ViewChangeHandler(activity: TrackingActivity) : Handler() {
        private val mActivity: WeakReference<TrackingActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            activity?.handleSecondMessage(msg)
        }
    }

    private fun handleSecondMessage(msg: Message) {
        tv_text.text = setIntToTime(secondForView)
    }

    private var mapTrackingServiceConnection: ServiceConnection = object : ServiceConnection {

        private val mCallback = object : MapTrackingService.ICallback {
            override fun sendLocation(location: LatLng, accuracy: Float) {
                myLocationMarker.position = location
                //arrayPoints.add(locate)
                tv_acc.text = accuracy.toString()
                polylineOptions.add(location)
                polyline?.remove()
                polyline = mMap.addPolyline(polylineOptions)
                if (isService)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(location))
            }

            override fun sendSecond(second: Int) {
                secondForView = second
                viewHandler.sendMessage(viewHandler.obtainMessage())
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
            mapService = mb.service // 서비스가 제공하는 메소드 호출하여
            mapService.registerCallback(mCallback)
            // 서비스쪽 객체를 전달받을수 있슴
            isService = mapService.isRunning

            val location = mapService.getLastLocation()
            //서울 위치
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val myLocation = LatLng(lat, lng)
                myLocationMarker = mMap.addMarker(MarkerOptions().position(myLocation))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }

            //서비스가 돌고 있을 때
            if (isService) {
                polylineOptions.addAll(mapService.getLocationList())
                polyline = mMap.addPolyline(polylineOptions)
                startRecordView()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false
        }
    }

    private fun startRecordView() {
        tv_text.visibility = View.VISIBLE
        btn_stop.visibility = View.VISIBLE
        img_midMarker.visibility = View.INVISIBLE
        btn_play.visibility = View.INVISIBLE
    }

    private fun stopRecordView() {
        btn_stop.visibility = View.INVISIBLE
        img_midMarker.visibility = View.VISIBLE
        btn_play.visibility = View.VISIBLE
        tv_text.visibility = View.GONE
    }

    private fun setIntToTime(timeInt: Int): String {

        var min = timeInt / 60
        val hour = min / 60
        val sec = timeInt % 60
        min %= 60

        return "${String.format("%02d", hour)}:${String.format("%02d", min)}:${String.format("%02d", sec)}"
    }

    private fun doBindService() {
        bindService(
            Intent(this, MapTrackingService::class.java),
            mapTrackingServiceConnection, Context.BIND_AUTO_CREATE
        )
        isBound = true
    }

    private fun doUnbindService() {
        if (isBound) {
            unbindService(mapTrackingServiceConnection)
            isBound = false
        }
    }
}