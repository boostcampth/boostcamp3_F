package com.boostcamp.travery.mapservice

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.Suggestion
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityTrackingBinding
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.boostcamp.travery.useraction.detail.UserActionDetailActivity
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import com.boostcamp.travery.utils.CustomMarker
import com.boostcamp.travery.utils.toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orhanobut.dialogplus.DialogPlus
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_tracking.*
import kotlinx.android.synthetic.main.item_dialog_footer.*
import java.lang.Exception

class TrackingActivity : BaseActivity<ActivityTrackingBinding>(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override val layoutResourceId: Int
        get() = R.layout.activity_tracking

    lateinit var mapService: MapTrackingService
    private lateinit var mMap: GoogleMap
    private lateinit var myLocationMarker: Marker
    private var suggestionMarker: Marker? = null
    private var locationCircle: Circle? = null
    private var polyline: Polyline? = null
    private var polylineOptions: PolylineOptions = PolylineOptions()
    private var isBound = false
    private val userActionMarkers = HashMap<Long, Marker>()


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
        mMap.setOnMarkerClickListener(this)

        polylineOptions.color(Color.BLUE)
                .geodesic(true)
                .width(10f)

        doBindService()

        viewModel.curLocation.observe(this, Observer {
            val latLng = LatLng(it.latitude, it.longitude)
            myLocationMarker.position = latLng

            if (locationCircle == null) {
                locationCircle = mMap.addCircle(CircleOptions()
                        .center(LatLng(it.latitude, it.longitude))
                        .radius(it.accuracy.toDouble())
                        .strokeWidth(1f)
                        .strokeColor(Color.parseColor("#884169e1"))
                        .fillColor(Color.parseColor("#5587cefa")))
            } else {
                locationCircle?.apply {
                    center = latLng
                    radius = it.accuracy.toDouble()
                }
            }

            if (viewModel.getIsServiceState()) {
                polylineOptions.add(latLng)
                polyline?.remove()
                polyline = mMap.addPolyline(polylineOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        })
    }

    override fun onDestroy() {
        doUnbindService()
        super.onDestroy()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        when (marker.tag) {
            null -> return false
            else -> {
                startActivityForResult(Intent(this, UserActionDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_USER_ACTION, viewModel.getUserAction(marker.tag as Long))
                }, Constants.REQUEST_CODE_USERACTION_EDIT)
            }
        }
        return true
    }

    fun startService(v: View) {
        if (viewModel.isFindGPS.get()) {
            val serviceIntent = Intent(this, MapTrackingService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            polylineOptions = PolylineOptions()
                    .color(Color.BLUE)
                    .geodesic(true)
                    .width(10f)
            polylineOptions.add(myLocationMarker.position)
        } else {
            getString(R.string.string_mapservice_gps_error).toast(this)
        }
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
        if (viewModel.totalDistance >= 5) {
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

            doUnbindService()

            val serviceIntent = Intent(this, MapTrackingService::class.java)
            stopService(serviceIntent)
            removeSuggestionMarker()
            mMap.clear()

            doBindService()

        } else {
            AlertDialog.Builder(this).apply {
                setMessage(resources.getString(R.string.string_save_course_error))
                setPositiveButton(resources.getString(R.string.dialog_positive)) { _, _ ->
                    doUnbindService()

                    val serviceIntent = Intent(this@TrackingActivity, MapTrackingService::class.java)
                    stopService(serviceIntent)
                    removeSuggestionMarker()
                    mMap.clear()

                    doBindService()
                }
                setNegativeButton(resources.getString(R.string.dialog_negative)) { dialog, _ ->
                    dialog.cancel()
                }
            }.create().show()
            //getString(R.string.string_save_course_error).toast(this)
        }
    }

    fun saveUserAction(v: View) {
        mapService.setCanSuggestFalse()
        startActivityForResult(Intent(this, UserActionSaveActivity::class.java).apply {
            putExtra(Constants.EXTRA_LATITUDE, myLocationMarker.position.latitude)
            putExtra(Constants.EXTRA_LONGITUDE, myLocationMarker.position.longitude)
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

        override fun onServiceConnected(
                name: ComponentName,
                service: IBinder) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            val mb = service as MapTrackingService.LocalBinder
            mapService = mb.service // 서비스가 제공하는 메소드 호출하여

            //서비스가 돌고 있을 때
            if (viewModel.getIsServiceState()) {
                myLocationMarker.position = viewModel.getTimeCodeList().last().coordinate
                Completable.fromAction {
                    viewModel.getTimeCodeList().forEach {
                        polylineOptions.add(it.coordinate)
                    }
                    viewModel.userActionList.forEach {
                        addCustomMarker(it.value)
                    }
                }.doOnComplete {
                    polyline = mMap.addPolyline(polylineOptions)
                }.subscribe().dispose()
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationMarker.position, 15f))
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
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

    private fun addCustomMarker(userAction: UserAction) {
        userActionMarkers[userAction.date.time] = mMap.addMarker(MarkerOptions().position(
                LatLng(userAction.latitude, userAction.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this@TrackingActivity, userAction.mainImage)))).apply { tag = userAction.date.time }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_USERACTION -> data?.let {
                    val userAction: UserAction = it.getParcelableExtra(Constants.EXTRA_USER_ACTION)

                    addCustomMarker(userAction)
                    viewModel.addUserAction(userAction)

                }

                Constants.REQUEST_CODE_USERACTION_EDIT -> data?.let {
                    val state = it.getStringExtra(Constants.EXTRA_USERACTION_STATE)
                    when (state) {
                        Constants.DELETE_STATE -> {
                            val time: Long? = it.getLongExtra(Constants.EXTRA_USER_ACTION_DATE, 0L)
                            time?.let { _time ->
                                viewModel.deleteUserAction(_time)
                                userActionMarkers[_time]?.remove()
                                userActionMarkers.remove(_time)
                            }
                        }

                        //삭제했을때는 여기서 실행
                        //수정했을 때는 여기서 실행
                        //TODO 우선 삭제하고 새로 넣는 방식으로 구현
                        //TODO hashmap 데이터를 변경하는 것도 생각해봐야함
                        Constants.EDIT_STATE -> {
                            try {
                                val userAction: UserAction = it.getParcelableExtra(Constants.EXTRA_USER_ACTION)
                                Completable.fromAction {
                                    viewModel.deleteUserAction(userAction.date.time)
                                    userActionMarkers[userAction.date.time]?.remove()
                                    userActionMarkers.remove(userAction.date.time)

                                }.doOnComplete {
                                    addCustomMarker(userAction)
                                    viewModel.addUserAction(userAction)
                                }.subscribe().dispose()

                            } catch (e: Exception) {
                            }
                        }

                        else -> return
                    }
                }
            }
        }
    }
}