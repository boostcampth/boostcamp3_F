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
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.boostcamp.travery.save.UserActionSaveActivity
import com.boostcamp.travery.utils.toast
import com.google.android.gms.maps.model.*
import io.reactivex.Completable
import java.lang.ref.WeakReference


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapService: MapTrackingService
    var isService = false
    private lateinit var mMap: GoogleMap
    private lateinit var myLocationMarker: Marker
    private var polyline: Polyline? = null
    private var polylineOptions: PolylineOptions = PolylineOptions()
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
        isService = true
        polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .geodesic(true)
                .width(10f)
        polylineOptions.add(myLocationMarker.position)
    }

    fun stopService(v: View) {
        if (mapService.getTotalDistance() >= 10) {
            val saveIntent = Intent(this@TrackingActivity, CourseSaveActivity::class.java)
                    .apply {
                        putParcelableArrayListExtra(Constants.EXTRA_COURSE_LOCATION_LIST, mapService.getTimeCodeList())
                        putExtra(
                                Constants.EXTRA_COURSE,
                                Course(
                                        "",
                                        "",
                                        "",
                                        mapService.getStartTime(),
                                        mapService.getEndTime(),
                                        mapService.getTotalDistance(),
                                        mapService.getStartTime().toString(),
                                        mapService.getStartTime().toString()
                                )
                        )
                    }
            startActivity(saveIntent)
        } else getString(R.string.string_save_course_error).toast(this)

        stopRecordView()
        doUnbindService()

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        stopService(serviceIntent)

        mMap.clear()

        doBindService()
        //finish()
    }

    fun saveUserAction(v: View) {
        startActivity(Intent(this, UserActionSaveActivity::class.java).apply {
            putExtra(Constants.EXTRA_LATITUDE, myLocationMarker.position.latitude)
            putExtra(Constants.EXTRA_LONGITUDE, myLocationMarker.position.longitude)
            putExtra(Constants.EXTRA_COURSE_CODE, mapService.getStartTime())
        })
    }

    fun gotoMyLocation(v: View) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationMarker.position, 15f))
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

                if (isService) {
                    polylineOptions.add(location)
                    polyline?.remove()
                    polyline = mMap.addPolyline(polylineOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(location))
                }
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
            var myLocation = LatLng(37.56, 126.97)
            myLocationMarker = mMap.addMarker(
                    MarkerOptions()
                            .position(myLocation)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_position_no_heading))
            )
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                myLocation = LatLng(lat, lng)
                myLocationMarker.position = myLocation
                //myLocationMarker = mMap.addMarker(MarkerOptions().position(myLocation))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }

            //서비스가 돌고 있을 때
            if (isService) {
                Completable.fromAction {
                    mapService.getTimeCodeList().forEach {
                        polylineOptions.add(it.coordinate)
                    }
                }.doOnComplete {
                    polyline = mMap.addPolyline(polylineOptions)
                }.subscribe()

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