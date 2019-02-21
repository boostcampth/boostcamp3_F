package com.boostcamp.travery.useraction.save

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityFindLocationBinding
import com.boostcamp.travery.utils.toLatLng
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_find_location.*
import java.util.*

class FindLocationActivity : BaseActivity<ActivityFindLocationBinding>(), OnMapReadyCallback {
    override val layoutResourceId: Int
        get() = R.layout.activity_find_location

    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null

    private var viewModel: UserActionSaveViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        (map_view as SupportMapFragment).getMapAsync(this)

        viewModel = ViewModelProviders.of(this).get(UserActionSaveViewModel::class.java)

        fab.setOnClickListener {
            marker?.run {
                showDialog(position)
            }
        }

        btn_myLocation.setOnClickListener {
            val curLocation = viewModel?.getLastKnownLocation()?.toLatLng() ?: LatLng(0.0, 0.0)
            gotoMyLocation(curLocation)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        initAutoComplete()
        initLocation()

        map.setOnMarkerClickListener {
            // 마커 클릭시 이벤트
            false
        }

        map.setOnMapClickListener {
            gotoMyLocation(it)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
    }

    private fun initAutoComplete() {
        val autocompleteFragment = supportFragmentManager
                .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.apply {
            setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME))
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    place.run {
                        gotoMyLocation(latLng ?: LatLng(0.0, 0.0), name)
                    }
                }

                override fun onError(status: Status) {
                    Log.i("MAP TEST", "An error occurred: $status")
                }
            })
        }
    }

    private fun initLocation() {
        // 이전 액티비티에서 현재 위치를 보내왔다면 그대로 사용
        // 아닐 경우 현재 위치로 이동
        val initLocation: LatLng = intent.extras?.getParcelable(Constants.EXTRA_LAT_LNG)
                ?: viewModel?.getLastKnownLocation()?.toLatLng() ?: LatLng(0.0, 0.0)

        gotoMyLocation(initLocation)
    }

    private fun gotoMyLocation(location: LatLng, title: String? = "") {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        marker?.remove()
        marker = googleMap.addMarker(
                MarkerOptions()
                        .title(title)
                        .position(location)
                        .flat(true))
    }

    private fun showDialog(latLng: LatLng) {
        AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.string_dialog_message_select_location))
            setPositiveButton(resources.getString(R.string.dialog_positive)) { _, _ ->
                // 선택 완료
                val intent = Intent().apply {
                    putExtra(Constants.EXTRA_LAT_LNG, latLng)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            setNegativeButton(resources.getString(R.string.dialog_negative)) { dialog, _ ->
                // 선택 취소
                dialog.cancel()
            }
        }.create().show()
    }
}
