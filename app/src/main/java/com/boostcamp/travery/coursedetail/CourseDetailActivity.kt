package com.boostcamp.travery.coursedetail


import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityCourseDetailBinding
import com.boostcamp.travery.utils.CustomMaker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_course_detail.*


class CourseDetailActivity : BaseActivity<ActivityCourseDetailBinding>(), OnMapReadyCallback, OnMarkerClickListener {
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
        viewModel.loadUserActionList()
        observeViewmodel()

    }

    private fun setupBindings(savedInstanceState: Bundle?) {
        //저장된 것이 없다면 새로 생성된 것으로 간주
        //intent로 받은 course를 viewmodel로 넘겨 초기화
        savedInstanceState?.let {}
                ?: viewModel.init(intent.getParcelableExtra(Constants.EXTRA_COURSE))
        viewDataBinding.viewModel = viewModel
        viewDataBinding.setLifecycleOwner(this)
        //상단 리사이클러뷰 터치 막기
        rv_useraction_toplist.setOnTouchListener { v, event -> true }
        //화면상 좌 리사이클러뷰 스크롤
        rv_useraction_leftlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //화면상 왼쪽 리사이클러뷰를 스크롤시 상단 아이템도 바뀌여야하므로 스크롤
                rv_useraction_toplist.scrollBy(0, dy)
            }
        })
        rv_useraction_leftlist.setOnSnapListener { viewModel.updatePosition(it) }
    }

    /**
     * googleMap Callback
     * @param 구글 맵
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.tag?.let { viewModel.markerClick(it as Int) }

        return false
    }

    fun observeViewmodel() {

        viewModel.latLngList.observe(this, Observer {
            // 경로의 폴리라인과 시작점 끝점 마크를 맵위에 표시.
            map.addPolyline(PolylineOptions().addAll(it))

            map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            LatLngBounds.builder().include(it[0]).include(it[it.size - 1]).build(),
                            100
                    )
            )
        })

        viewModel.markerList.observe(this, Observer { actionList ->
            for (i in actionList.indices) {
                when (i) {
                    0 -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start))).tag=i
                    actionList.size - 1 -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive))).tag=i
                    else -> map.addMarker(MarkerOptions().position(LatLng(actionList[i].latitude, actionList[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(CustomMaker.create(this, actionList[i].mainImage)))).tag = i
                }
            }
        })
        viewModel.curUseraction.observe(this, Observer {
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude,it.longitude)))
        })
    }

}