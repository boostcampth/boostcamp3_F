package com.boostcamp.travery.presentation.course.detail


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.Constants.EXTRA_USER_ACTION
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityCourseDetailBinding
import com.boostcamp.travery.presentation.useraction.detail.UserActionDetailActivity
import com.boostcamp.travery.utils.CustomMarker
import com.boostcamp.travery.utils.DateUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_course_detail.*
import kotlinx.android.synthetic.main.item_seekbar_content.view.*
import java.util.*
import kotlin.collections.HashMap


class CourseDetailActivity : BaseActivity<ActivityCourseDetailBinding>(), OnMapReadyCallback {
    override val layoutResourceId: Int = R.layout.activity_course_detail
    private lateinit var seekerMarker: Marker
    private val markersHashMap = HashMap<Long, Marker>()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(CourseDetailViewModel::class.java)
    }

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewDataBinding.root)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val mapFragment = fragment_map as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupBindings(savedInstanceState)
        rv_useraction_list.setOnSnapListener {
            viewModel.updateCurUseraction(it)
        }

        viewModel.setEventListener(object : CourseDetailViewModel.ViewModelEventListener {
            override fun onItemClick(item: Any) {
                if (item is UserAction) {
                    startActivity(Intent(this@CourseDetailActivity, UserActionDetailActivity::class.java).apply { putExtra(EXTRA_USER_ACTION, item) })
                }
            }
        })
    }

    private fun setupBindings(savedInstanceState: Bundle?) {
        //저장된 것이 없다면 새로 생성된 것으로 간주
        //intent로 받은 course를 viewmodel로 넘겨 초기화
        savedInstanceState?.let {}
                ?: viewModel.init(intent.getParcelableExtra(Constants.EXTRA_COURSE))
        viewDataBinding.viewModel = viewModel
        viewDataBinding.setLifecycleOwner(this)

    }

    /**
     * googleMap Callback
     * @param 구글 맵
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener { marker ->
            marker?.tag?.let { viewModel.markerClick(it as UserAction) }
            false
        }
        observeViewModel()
    }


    private fun observeViewModel() {

        viewModel.latLngList.observe(this, Observer {
            // 경로의 폴리라인과 시작점 끝점 마크를 맵위에 표시.
            val polylineOptions =
                    PolylineOptions().color(ContextCompat.getColor(this, R.color.gray_alpha50))
                            .geodesic(true)
                            .width(10f)

            val boundsBuilder = LatLngBounds.Builder()
            val routePadding = 220

            it.forEach { latlng ->
                polylineOptions.add(latlng)
                boundsBuilder.include(latlng)
            }
            val latLngBounds = boundsBuilder.build()

            map.addPolyline(polylineOptions).also { polyline -> polyline.pattern = Arrays.asList(Gap(20f), Dash(20f)) }

            //seekbar max 설정
            detail_seekbar.max = viewModel.timeCodeListSize.get().toFloat()

            //지도가 완전히 뜨고나서 카메라가 지정한 위치로 이동하기 위해 post를 사용.
            fragment_map.view?.post {
                map.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding)
                )
                seekerMarker = map.addMarker(
                        MarkerOptions()
                                .position(it.first())
                                .flat(true)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_position_no_heading))
                )
            }

        })

        viewModel.seekProgress.observe(this, Observer {
            detail_seekbar.setProgress(it.toFloat())
        })

        viewModel.seekTimeCode.observe(this, Observer {
            seekerMarker.position = it.coordinate
            map.animateCamera(CameraUpdateFactory.newLatLng(it.coordinate), 150, null)
            detail_seekbar.indicator.topContentView.tv_time?.text = DateUtils.parseDateAsString(it.timeStamp, "a h시 mm분 ss초")
        })

        viewModel.markerList.observe(this, Observer { actionList ->
            for (i in 0 until actionList.size) {
                with(actionList[i]) {
                    when (i) {
                        0 -> {
                            markersHashMap[this.date.time] = map.addMarker(MarkerOptions()
                                    .position(LatLng(this.latitude, this.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start)))
                            markersHashMap[this.date.time]?.tag = this
                        }
                        actionList.size - 1 -> {
                            markersHashMap[this.date.time] = map.addMarker(MarkerOptions().position(LatLng(this.latitude, this.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive)))
                            markersHashMap[this.date.time]?.tag = this
                        }
                        else -> {
                            markersHashMap[this.date.time] = map.addMarker(MarkerOptions().position(LatLng(this.latitude, this.longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this@CourseDetailActivity, this.mainImage))))
                            markersHashMap[this.date.time]?.tag = this
                        }
                    }
                    Log.d("testlog", this.courseCode.toString())
                }
            }
        })

        viewModel.curUseraction.observe(this, Observer {
            map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
        })

        viewModel.userActionStateChange.observe(this, Observer {
            when (it.state) {
                Constants.EDIT_STATE -> {
                    markersHashMap[it.userAction.date.time]?.remove()
                    markersHashMap[it.userAction.date.time] = map.addMarker(MarkerOptions().position(LatLng(it.userAction.latitude, it.userAction.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this@CourseDetailActivity, it.userAction.mainImage))))
                            .apply { tag = it.userAction }
                }
                Constants.DELETE_STATE -> {
                    markersHashMap[it.userAction.date.time]?.remove()
                    markersHashMap.remove(it.userAction.date.time)
                }
            }
        })
    }

}