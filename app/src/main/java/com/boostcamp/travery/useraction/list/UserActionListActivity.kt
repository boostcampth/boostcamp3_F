package com.boostcamp.travery.useraction.list


import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityUserActionListBinding
import com.boostcamp.travery.useraction.list.cluster.ClusterItemUserAction
import com.boostcamp.travery.useraction.list.cluster.ClusterRenderer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_user_action_list.*


class UserActionListActivity : BaseActivity<ActivityUserActionListBinding>(),
        OnMapReadyCallback,
        UserActionListViewModel.Contract,
        ClusterManager.OnClusterClickListener<ClusterItemUserAction>,
        ClusterManager.OnClusterInfoWindowClickListener<ClusterItemUserAction>,
        ClusterManager.OnClusterItemClickListener<ClusterItemUserAction>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItemUserAction> {
    override val layoutResourceId: Int
        get() = com.boostcamp.travery.R.layout.activity_user_action_list

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserActionListViewModel::class.java)
    }

    private var googleMap: GoogleMap? = null
    private var curLocation: LatLng? = null
    private var marker: Marker? = null
    private var clusterManager: ClusterManager<ClusterItemUserAction>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        (map as SupportMapFragment).getMapAsync(this)

        viewModel.setContract(this)

        observeCurrentLocation()

        // 현재 위치 표시 및 카메라 이동
        btn_myLocation.setOnClickListener {
            marker?.remove()
            viewModel.setCurrentLocation()
            curLocation?.let { loc -> moveCamera(loc) }
        }
    }

    // 현재 위치 받아오기
    private fun observeCurrentLocation() {
        viewModel.setCurrentLocation()
        viewModel.getCurLocation().observe(this, Observer {
            curLocation = it.toLatLng().also { loc ->
                marker = googleMap?.addMarker(
                        MarkerOptions()
                                .position(loc)
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_position_no_heading)))
            }
        })
    }

    // 해당 위치로 카메라 이동
    private fun moveCamera(location: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // 클러스터링 초기화
    private fun settingClustering() {
        if (googleMap != null) {
            clusterManager = ClusterManager(this, googleMap)
            googleMap?.apply {
                setOnCameraIdleListener(clusterManager)
                setOnMarkerClickListener(clusterManager)
                setOnInfoWindowClickListener(clusterManager)
            }
        }

        clusterManager?.apply {
            this.renderer = ClusterRenderer(this@UserActionListActivity, googleMap!!, clusterManager!!)
            setOnClusterClickListener(this@UserActionListActivity)
            setOnClusterInfoWindowClickListener(this@UserActionListActivity)
            setOnClusterItemClickListener(this@UserActionListActivity)
            setOnClusterItemInfoWindowClickListener(this@UserActionListActivity)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        viewModel.setCurrentLocation()

        this.googleMap = map
        settingClustering()
    }

    override fun onUserActionLoading(list: List<UserAction>) {
        if (list.isNotEmpty()) {
            list.forEach {
                clusterManager?.addItem(ClusterItemUserAction(it.title, it.mainImage, LatLng(it.latitude, it.longitude)))
            }

            // 초기 화면 위치. 현재 위치를 읽어왔다면 현재위치, 그렇지 않은 경우 첫번째 활동 위치 기준
            moveCamera(curLocation ?: LatLng(list[0].latitude, list[0].longitude))

            clusterManager?.cluster()
        } else {
            curLocation?.let { moveCamera(it) }
        }
    }

    // 아이템 클릭 시 줌 인
    override fun onClusterClick(cluster: Cluster<ClusterItemUserAction>): Boolean {
        // 아이템들이 화면 안에 포함되도록 보여주기 위해 바운더리 계산 필요

        val builder = LatLngBounds.builder()
        for (item in cluster.items) {
            builder.include(item.position)
        }

        // LatLngBounds 계산
        val bounds = builder.build()

        try {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    override fun onClusterInfoWindowClick(cluster: Cluster<ClusterItemUserAction>?) {

    }

    override fun onClusterItemClick(cluster: ClusterItemUserAction?): Boolean {

        return false
    }

    override fun onClusterItemInfoWindowClick(cluster: ClusterItemUserAction?) {

    }

    private fun Location.toLatLng(): LatLng {
        return LatLng(this.latitude, this.longitude)
    }
}
