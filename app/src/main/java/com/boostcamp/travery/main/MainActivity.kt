package com.boostcamp.travery.main


import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.coursedetail.CourseDetailActivity
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ActivityMainBinding
import com.boostcamp.travery.mapservice.TrackingActivity
import com.boostcamp.travery.mapservice.savecourse.CourseSaveActivity
import com.boostcamp.travery.search.SearchResultActivity
import com.google.android.material.navigation.NavigationView
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationView.OnNavigationItemSelectedListener,
        MainViewModel.Contract {
    private val GPS_ENABLE_REQUEST_CODE = 2001

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewDataBinding.viewmodel = viewModel

        viewModel.loadCourseList()
        viewModel.setViewModelContract(this)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
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
                            //"Permission Denied\n" + tedPermissionResult.deniedPermissions.toString().toast()
                        }
                    }, { })
        }

        ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ).also {
            drawer_layout.addDrawerListener(it)
            it.syncState()
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivity(Intent(this, CourseSaveActivity::class.java))
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
        if (item is Course) {
            startActivity(Intent(this, CourseDetailActivity::class.java).apply {
                putExtra("course", item)
            })
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showDialogForLocationServiceSetting() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(getString(R.string.permission_dialog_gps_title))
            setMessage(getString(R.string.permission_dialog_gps_description))
            setCancelable(true)
            setPositiveButton(getString(R.string.all_setting)) { _, _ ->
                val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            }
            setNegativeButton(getString(R.string.all_cancel)) { dialog, _ -> dialog.cancel() }
            create().show()
        }
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
