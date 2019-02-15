package com.boostcamp.travery.main


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.coursedetail.CourseDetailActivity
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ActivityMainBinding
import com.boostcamp.travery.mapservice.TrackingActivity
import com.boostcamp.travery.search.SearchResultActivity
import com.boostcamp.travery.useraction.list.UserActionFeedActivity
import com.boostcamp.travery.useraction.list.UserActionListActivity
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import com.google.android.material.navigation.NavigationView
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationView.OnNavigationItemSelectedListener,
    MainViewModel.View {
    companion object {
        private const val GPS_ENABLE_REQUEST_CODE = 2001
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {
            setView(this@MainActivity)
        }

        setSupportActionBar(toolBar as Toolbar)
        supportActionBar?.title = ""

        initFab()
        initFabAnimation()

        ActionBarDrawerToggle(
            this, drawer_layout, toolBar as Toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).also {
            drawer_layout.addDrawerListener(it)
            it.syncState()
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun initFab() {
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
    }

    private fun initFabAnimation() {
        rv_course_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isScrollUp = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val dy =
                if (newState != RecyclerView.SCROLL_STATE_IDLE && isScrollUp) {
                    (fab.height + (fab.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat()
                } else {
                    0F
                }

                fab.clearAnimation()
                fab.animate().translationY(dy).start()
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                isScrollUp = dy > 0
            }
        })
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
                onProgress(resources.getString(R.string.progress_bar_message))
                startActivity(Intent(this, UserActionListActivity::class.java))
            }
            R.id.nav_gallery -> {
//                startActivity(Intent(this, SearchResultActivity::class.java))
                startActivity(Intent(this, UserActionFeedActivity::class.java))
            }
            R.id.nav_slideshow -> {
                startActivity(Intent(this, UserActionSaveActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onItemClick(item: Any) {
        if (item is Course) {
            startActivity(Intent(this, CourseDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_COURSE, item)
            })
        }
    }

    private fun showDialogForLocationServiceSetting() {
        AlertDialog.Builder(this@MainActivity, R.style.dialogTheme).apply {
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
                }
        }
    }

    override fun onStop() {
        super.onStop()
        offProgress()
    }
}
