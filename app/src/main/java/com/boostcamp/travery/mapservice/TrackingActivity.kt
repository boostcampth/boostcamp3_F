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
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.boostcamp.travery.save.UserActionSaveActivity
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
    private var secondForView = 0
    private val viewHandler = ViewChangeHandler(this)
    private var isBound = false
    private var suggestAdapter: BaseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(TrackingViewModel::class.java)

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
        //startRecordView()
        viewDataBinding.viewmodel?.setIsServiceState(true)
        //isService = true
        polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .geodesic(true)
                .width(10f)
        polylineOptions.add(myLocationMarker.position)
    }

    fun stopService(v: View) {
        if (mapService.getSuggestList().size > 0) {
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
        dismissSuggestNoti()
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

        doUnbindService()

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        stopService(serviceIntent)

        mMap.clear()

        doBindService()
    }

    fun saveUserAction(v: View) {
        mapService.setCanSuggestFalse()
        startActivityForResult(Intent(this, UserActionSaveActivity::class.java).apply {
            when (viewDataBinding.viewmodel?.getIsServiceState()) {
                true -> {
                    putExtra(Constants.EXTRA_LATITUDE, myLocationMarker.position.latitude)
                    putExtra(Constants.EXTRA_LONGITUDE, myLocationMarker.position.longitude)
                }
                false -> {
                    putExtra(Constants.EXTRA_LATITUDE, mMap.cameraPosition.target.latitude)
                    putExtra(Constants.EXTRA_LONGITUDE, mMap.cameraPosition.target.longitude)
                }
            }
            putExtra(Constants.EXTRA_COURSE_CODE, mapService.getStartTime())
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

    @SuppressLint("ResourceAsColor")
    fun openSuggestDialog(v: View) {
        val list = mapService.getSuggestList()
        if (list.size == 0) return
        suggestAdapter = SuggestListAdapter(this@TrackingActivity, list)
        val dialog = DialogPlus.newDialog(this@TrackingActivity)
                .setAdapter(suggestAdapter)
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
                        mapService.removeSuggestItem(position)
                        tv_seggest_num.text = mapService.getSuggestList().size.toString()
                        if (tv_seggest_num.text == "0") dismissSuggestNoti()
                        removeSuggestionMarker()
                    }

                    footer_save_button.setOnClickListener {
                        startActivityForResult(Intent(this, UserActionSaveActivity::class.java).apply {
                            putExtra(Constants.EXTRA_LATITUDE, suggestionMarker!!.position.latitude)
                            putExtra(Constants.EXTRA_LONGITUDE, suggestionMarker!!.position.longitude)
                            putExtra(Constants.EXTRA_COURSE_CODE, mapService.getStartTime())
                        }, Constants.REQUEST_CODE_USERACTION)

                        mapService.removeSuggestItem(position)
                        tv_seggest_num.text = mapService.getSuggestList().size.toString()
                        if (tv_seggest_num.text == "0") dismissSuggestNoti()
                        removeSuggestionMarker()
                    }
                    dialog.dismiss()
                }
                .setCancelable(true)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create()
        dialog.show()
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

                if (viewDataBinding.viewmodel?.getIsServiceState() == true) {
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

            override fun saveInitCourse(startTime: Long) {
                AppDataManager(application, AppDbHelper.getInstance(application)).saveCourse(
                        Course(startTime = startTime)
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }


            override fun sendSuggestList(suggestList: ArrayList<Suggestion>) {
                suggestAdapter?.notifyDataSetChanged()
                showSuggestNoti(suggestList.size)
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
            viewDataBinding.viewmodel?.setIsServiceState(mapService.isRunning)

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }

            //서비스가 돌고 있을 때
            if (viewDataBinding.viewmodel?.getIsServiceState() == true) {
                Completable.fromAction {
                    mapService.getTimeCodeList().forEach {
                        polylineOptions.add(it.coordinate)
                    }
                }.doOnComplete {
                    polyline = mMap.addPolyline(polylineOptions)
                }.subscribe().dispose()

                showSuggestNoti(mapService.getSuggestList().size)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            viewDataBinding.viewmodel?.setIsServiceState(false)
        }
    }

    private fun showSuggestNoti(listSize: Int) {
        if (listSize > 0) {
            btn_suggest.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_alert_red_24dp))
            tv_seggest_num.text = listSize.toString()
        }
    }

    private fun dismissSuggestNoti() {
        btn_suggest.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_alert_black_24dp))
        tv_seggest_num.text = "0"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_USERACTION -> data?.let {
                    mMap.addMarker(MarkerOptions().position(
                            LatLng(it.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0),
                                    it.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0)))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)))
                }
            }
        }
    }
}