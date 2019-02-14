package com.boostcamp.travery.coursedetail


import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.Constants.EXTRA_USER_ACTION
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityCourseDetailBinding
import com.boostcamp.travery.useraction.detail.UserActionDetailActivity
import com.boostcamp.travery.utils.CustomMarker
import com.boostcamp.travery.utils.toPx
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_course_detail.*


class CourseDetailActivity : BaseActivity<ActivityCourseDetailBinding>(), OnMapReadyCallback {
    override val layoutResourceId: Int = R.layout.activity_course_detail
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(CourseDetailViewModel::class.java)
    }

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewDataBinding.root)
        val mapFragment = fragment_map as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupBindings(savedInstanceState)
        rv_useraction_list.setOnSnapListener { viewModel.updateCurUseraction(it) }
        viewModel.setEventListener(object : CourseDetailViewModel.ViewModelEventListener {
            override fun onItemClick(item: Any) {
                if(item is UserAction){
                    startActivity(Intent(this@CourseDetailActivity,UserActionDetailActivity::class.java).apply { putExtra(EXTRA_USER_ACTION,item) })
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
            marker?.tag?.let { viewModel.markerClick(it as Int) }
            false
        }
        map.setOnMapClickListener {
            viewModel.mapClick()
        }
        observeViewModel()
    }


    private fun observeViewModel() {

        viewModel.latLngList.observe(this, Observer {
            // 경로의 폴리라인과 시작점 끝점 마크를 맵위에 표시.
            map.addPolyline(PolylineOptions().addAll(it))

            //지도가 완전히 뜨고나서 카메라가 지정한 위치로 이동하기 위해 post를 사용.
            fragment_map.view?.post {
                map.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                LatLngBounds.builder().include(it[0]).include(it[it.size - 1]).build()
                                , 110.toPx())
                )
            }

        })

        viewModel.markerList.observe(this, Observer { actionList ->
            for (i in actionList.indices) {
                when (i) {
                    0 -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start))).tag = i
                    actionList.size - 1 -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive))).tag = i
                    else -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this, actionList[i].mainImage)))).tag = i
                }
            }
        })
        viewModel.curUseraction.observe(this, Observer {
            map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
        })
    }

}