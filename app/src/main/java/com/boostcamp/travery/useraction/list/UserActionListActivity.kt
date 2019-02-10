package com.boostcamp.travery.useraction.list

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityUserActionListBinding
import com.boostcamp.travery.utils.CustomMarker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_user_action_list.*

class UserActionListActivity : BaseActivity<ActivityUserActionListBinding>(), OnMapReadyCallback, UserActionListViewModel.Contract {
    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_list

    private var googleMap: GoogleMap? = null
    private var curLocation: Location? = null

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserActionListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        (map as SupportMapFragment).getMapAsync(this)

        viewModel.setContract(this)

        viewModel.getCurLocation().observe(this, Observer {
            curLocation = it
            // 현재 위치 표시
            googleMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_position_no_heading)))
        })
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        viewModel.setCurrentLocation()
        onProgress(resources.getString(R.string.progress_bar_message))
    }

    override fun onUserActionLoading(list: List<UserAction>) {
        list.forEach {
            googleMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this, it.mainImage))))
                    ?.title = it.title
        }

        // 초기 화면 위치. 현재 위치를 읽어왔다면 현재위치, 그렇지 않은 경우 첫번째 활동 위치 기준
        val startPosition = curLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(list[0].latitude, list[0].longitude)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 15f))

        offProgress()
    }
}
