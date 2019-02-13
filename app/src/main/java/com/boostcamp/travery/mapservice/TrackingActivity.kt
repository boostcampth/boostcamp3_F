package com.boostcamp.travery.mapservice

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import com.boostcamp.travery.utils.toast
import com.google.android.gms.maps.model.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import com.orhanobut.dialogplus.DialogPlus
import android.view.Gravity
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.databinding.ActivityTrackingBinding
import kotlinx.android.synthetic.main.item_dialog_footer.*

class TrackingActivity : BaseActivity<ActivityTrackingBinding>(), OnMapReadyCallback {
    override val layoutResourceId: Int
        get() = R.layout.activity_tracking

    lateinit var mapService: MapTrackingService
    //var isService = false
    private lateinit var mMap: GoogleMap
    private lateinit var myLocationMarker: Marker
    private var suggestionMarker: Marker? = null
    private var polyline: Polyline? = null
    private var polylineOptions: PolylineOptions = PolylineOptions()
    private var isBound = false

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(TrackingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        viewDataBinding.viewmodel = viewModel

        val mapFragment = map_trackingActivity as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        polylineOptions.color(Color.BLUE)
                .geodesic(true)
                .width(10f)

        doBindService()

        viewModel.curLocation.observe(this, Observer {
            myLocationMarker.position = it

            if (viewModel.getIsServiceState()) {
                polylineOptions.add(it)
                polyline?.remove()
                polyline = mMap.addPolyline(polylineOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(it))
            }
        })
    }

    override fun onDestroy() {
        doUnbindService()
        super.onDestroy()
    }

    fun startService(v: View) {
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        //startRecordView()
        viewModel.setIsServiceState(true)
        //isService = true
        polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .geodesic(true)
                .width(10f)
        polylineOptions.add(myLocationMarker.position)
    }

    fun stopService(v: View) {
        if (viewModel.suggestCountString.get()?.toInt() ?: 0 > 0) {
            AlertDialog.Builder(this@TrackingActivity, R.style.dialogTheme).apply {
                setTitle(getString(R.string.suggestion_dialog_title))
                setMessage(getString(R.string.suggestion_dialog_description))
                setCancelable(true)
                setPositiveButton(getString(R.string.all_cancel)) { dialog, _ ->
                    dialog.cancel()
                }
                setNegativeButton(getString(R.string.all_ignore)) { dialog, _ ->
                    startCourseSaveActivity()
                    dialog.cancel()
                }
                create().show()
            }
        } else {
            startCourseSaveActivity()
        }
    }

    private fun startCourseSaveActivity() {
        if (viewModel.totalDistance >= 10) {
            val saveIntent = Intent(this@TrackingActivity, CourseSaveActivity::class.java)
                    .apply {
                        putParcelableArrayListExtra(Constants.EXTRA_COURSE_LOCATION_LIST, viewModel.getTimeCodeList())
                        putExtra(
                                Constants.EXTRA_COURSE,
                                Course(
                                        "",
                                        "",
                                        "",
                                        viewModel.startTime,
                                        System.currentTimeMillis(),
                                        viewModel.totalDistance,
                                        viewModel.startTime.toString(),
                                        viewModel.startTime.toString()
                                )
                        )
                    }
            startActivity(saveIntent)
        } else getString(R.string.string_save_course_error).toast(this)

        doUnbindService()

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        stopService(serviceIntent)

        mMap.clear()

        doBindService()
    }

    fun saveUserAction(v: View) {
        mapService.setCanSuggestFalse()
        startActivityForResult(Intent(this, UserActionSaveActivity::class.java).apply {
            when (viewModel.getIsServiceState()) {
                true -> {
                    putExtra(Constants.EXTRA_LATITUDE, myLocationMarker.position.latitude)
                    putExtra(Constants.EXTRA_LONGITUDE, myLocationMarker.position.longitude)
                }
                false -> {
                    putExtra(Constants.EXTRA_LATITUDE, mMap.cameraPosition.target.latitude)
                    putExtra(Constants.EXTRA_LONGITUDE, mMap.cameraPosition.target.longitude)
                }
            }
            putExtra(Constants.EXTRA_COURSE_CODE, viewModel.startTime)
        }, Constants.REQUEST_CODE_USERACTION)

    }

    fun gotoMyLocation(v: View) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationMarker.position, 15f))
    }

    private fun removeSuggestionMarker() {
        footer.visibility = View.GONE
        suggestionMarker?.remove()
        suggestionMarker = null
    }

    fun openSuggestDialog(v: View) {
        val dialog = DialogPlus.newDialog(this@TrackingActivity)
                .setAdapter(viewModel.getSuggestAdapter())
                .setGravity(Gravity.BOTTOM)
                .setOnItemClickListener { dialog, item, view, position ->
                    mMap.animateCamera(CameraUpdateFactory.newLatLng((item as Suggestion).location))

                    footer.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(footer, "alpha", 0f, 1f).apply {
                        duration = 500
                        start()
                    }
                    if (suggestionMarker == null) {
                        suggestionMarker = mMap.addMarker(
                                MarkerOptions()
                                        .position(item.location)
                                        .flat(true)
                        )
                    } else {
                        suggestionMarker?.position = item.location
                    }

                    footer_cancel_button.setOnClickListener {
                        removeSuggestionMarker()
                    }

                    footer_delete_button.setOnClickListener {
                        viewModel.removeSuggestItem(position)
                        removeSuggestionMarker()
                    }

                    footer_save_button.setOnClickListener {
                        startActivityForResult(Intent(this, UserActionSaveActivity::class.java).apply {
                            putExtra(Constants.EXTRA_LATITUDE, suggestionMarker!!.position.latitude)
                            putExtra(Constants.EXTRA_LONGITUDE, suggestionMarker!!.position.longitude)
                            putExtra(Constants.EXTRA_COURSE_CODE, viewModel.startTime)
                        }, Constants.REQUEST_CODE_USERACTION)

                        viewModel.removeSuggestItem(position)
                        removeSuggestionMarker()
                    }
                    dialog.dismiss()
                }
                .setCancelable(true)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create()
        dialog.show()
    }

    private var mapTrackingServiceConnection: ServiceConnection = object : ServiceConnection {

        private val mCallback = object : MapTrackingService.ICallback {

            override fun saveInitCourse(startTime: Long) {
                AppDataManager(application, AppDbHelper.getInstance(application)).saveCourse(
                        Course(startTime = startTime)
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
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
            viewModel.setIsServiceState(mapService.isRunning)

            val location = mapService.getLastLocation()
            //서울 위치

            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val myLocation = LatLng(lat, lng)
                myLocationMarker.position = myLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }

            //서비스가 돌고 있을 때
            if (viewModel.getIsServiceState()) {
                Completable.fromAction {
                    viewModel.getTimeCodeList().forEach {
                        polylineOptions.add(it.coordinate)
                    }
                    viewModel.userActionLocateList.forEach {
                        mMap.addMarker(MarkerOptions().position(it)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)))
                    }
                }.doOnComplete {
                    polyline = mMap.addPolyline(polylineOptions)
                }.subscribe().dispose()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            viewModel.setIsServiceState(false)
        }
    }

    private fun doBindService() {
        val myLocation = LatLng(37.56, 126.97)
        myLocationMarker = mMap.addMarker(
                MarkerOptions()
                        .position(myLocation)
                        .flat(true)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_position_no_heading))
        )

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_USERACTION -> data?.let {
                    if (viewModel.getIsServiceState()) {
                        mMap.addMarker(MarkerOptions().position(
                                LatLng(it.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0),
                                        it.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0)))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)))

                        viewModel.addUserActionLocate(LatLng(
                                it.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0),
                                it.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0)
                        ))
                    }
                }
            }
        }
    }
}