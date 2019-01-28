package com.boostcamp.travery.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.data.model.Route
import com.boostcamp.travery.dummy.RouteDummyData
import com.boostcamp.travery.main.adapter.RouteListAdapter
import com.boostcamp.travery.main.viewholder.GroupItem
import com.boostcamp.travery.mapservice.TrackingActivity
import com.boostcamp.travery.mapservice.saveroute.RouteSaveActivity
import com.boostcamp.travery.routedetail.RouteDetailActivity
import com.boostcamp.travery.search.SearchResultActivity
import com.boostcamp.travery.utils.DateUtils
import com.google.android.material.navigation.NavigationView
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import android.location.LocationManager
import android.content.Context
import com.boostcamp.travery.R


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnItemClickListener {
    private val adapter = RouteListAdapter(this)
    private val compositeDisposable = CompositeDisposable()
    private val GPS_ENABLE_REQUEST_CODE = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            TedRx2Permission.with(this)
                    .setRationaleTitle(getString(R.string.permission_title))
                    .setRationaleMessage(getString(R.string.permission_message)) // "we need permission for read contact and find your location"
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .request()
                    .subscribe({ tedPermissionResult ->
                        if (tedPermissionResult.isGranted) {
                            if (!checkLocationServicesStatus()) {
                                showDialogForLocationServiceSetting()
                            } else {
                                val intent = Intent(this@MainActivity, TrackingActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(
                                    this,
                                    "Permission Denied\n" + tedPermissionResult.getDeniedPermissions().toString(),
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                    }, { })


            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Flowable.just(RouteDummyData.getData())
            .map { list ->
                val ret = ArrayList<Any>()
                var partition = -1

                list.forEach { route ->
                    if (partition != DateUtils.getTermDay(toMillis = route.endTime)) {
                        ret.add(GroupItem("${DateUtils.getDate(route.endTime)[2]}"))
                    }
                    partition = DateUtils.getTermDay(toMillis = route.endTime)
                    ret.add(route)
                }

                ret
            }.subscribe(
                {
                    adapter.setItems(it)
                },
                {
                    Log.e("TAG", "List load error", it)
                }
            ).also { compositeDisposable.add(it) }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        rv_route_list.apply {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivity(Intent(this, RouteSaveActivity::class.java))
            }
            R.id.nav_gallery -> {
                startActivity(Intent(this, SearchResultActivity::class.java))
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onItemClick(item: Any) {
        if (item is Route) {
            var intent = Intent(this, RouteDetailActivity::class.java)
            intent.putExtra("route", item)
            startActivity(intent)
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showDialogForLocationServiceSetting() {

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.permission_dialog_gps_title))
        builder.setMessage(getString(R.string.permission_dialog_gps_description))
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    val intent = Intent(this@MainActivity, TrackingActivity::class.java)
                    startActivity(intent)
                }
        }
    }
}
