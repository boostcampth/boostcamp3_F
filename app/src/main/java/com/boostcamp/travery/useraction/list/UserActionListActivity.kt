package com.boostcamp.travery.useraction.list

import android.os.Bundle
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

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserActionListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        (map as SupportMapFragment).getMapAsync(this)

        viewModel.setContract(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        onProgress(resources.getString(R.string.progress_bar_message))
    }

    override fun onUserActionLoading(list: List<UserAction>) {
        list.forEach {
            googleMap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.create(this, it.mainImage))))
                    ?.title = it.title
        }
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(list[0].let { LatLng(it.latitude, it.longitude) }, 15f))
        offProgress()
    }
}
