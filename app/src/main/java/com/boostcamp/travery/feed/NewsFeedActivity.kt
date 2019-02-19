package com.boostcamp.travery.feed

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBinderMapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.community.SettingActivity
import com.boostcamp.travery.course.list.CourseListActivity
import com.boostcamp.travery.databinding.MainFeedBinding
import com.boostcamp.travery.databinding.NavHeaderMainBinding
import com.boostcamp.travery.mapservice.TrackingActivity
import com.boostcamp.travery.useraction.list.UserActionListActivity
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import com.google.android.material.navigation.NavigationView
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kotlinx.android.synthetic.main.main_feed.*
import kotlinx.android.synthetic.main.main_layout_newsfeed.*

class NewsFeedActivity : BaseActivity<MainFeedBinding>(), NavigationView.OnNavigationItemSelectedListener {
    override val layoutResourceId: Int = R.layout.main_feed
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(NewsFeedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        viewDataBinding.viewmodel = viewModel
        DataBindingUtil.bind<NavHeaderMainBinding>(viewDataBinding.navView.getHeaderView(0))?.user=viewModel.getPreferences()
        setSupportActionBar(toolBar as Toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        viewDataBinding.setLifecycleOwner(this)

        ActionBarDrawerToggle(
                this, drawer_layout, toolBar as Toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ).also {
            drawer_layout.addDrawerListener(it)
            it.syncState()
        }

        nav_view.setNavigationItemSelectedListener(this)

        //refreshListner onRefresh
        sl_feed.setOnRefreshListener {
            viewModel.refreshList()
        }
        viewModel.isLoding.observe(this, Observer {
            sl_feed.isRefreshing = it
        })

        initView()

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
            R.id.nav_recode_course->{
                permissionCheck()
            }

            R.id.nav_useraction_map -> {
                onProgress(resources.getString(R.string.progress_bar_message))
                startActivity(Intent(this, UserActionListActivity::class.java))
            }
            R.id.nav_useraction_list -> {
                startActivity(Intent(this, CourseListActivity::class.java))
            }
            R.id.nav_useraction_add -> {
                startActivity(Intent(this, UserActionSaveActivity::class.java))
            }

            R.id.nav_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun initView() {

        //코스 기록을 위한 버튼
        fab.setOnClickListener {
            permissionCheck()
        }
        rv_newsfeed_list.layoutManager = LinearLayoutManager(this)
        rv_newsfeed_list.adapter = NewsFeedListAdapter(viewModel.getFeedList())
        rv_newsfeed_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isAnimated = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                if (totalItemCount == (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()) {
                    viewModel.loadFeedList()
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isAnimated = false
                    fab.animate().translationY(0F).withLayer()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !isAnimated) {
                    isAnimated = true
                    val moveDistance = (fab.height + (fab.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat()
                    fab.animate().translationY(moveDistance).withLayer()
                }
            }
        })
    }


    private fun showDialogForLocationServiceSetting() {
        AlertDialog.Builder(this@NewsFeedActivity, R.style.dialogTheme).apply {
            setTitle(getString(R.string.permission_dialog_gps_title))
            setMessage(getString(R.string.permission_dialog_gps_description))
            setCancelable(true)
            setPositiveButton(getString(R.string.all_setting)) { _, _ ->
                val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, Constants.GPS_ENABLE_REQUEST_CODE)
            }
            setNegativeButton(getString(R.string.all_cancel)) { dialog, _ -> dialog.cancel() }
            create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                }
        }
    }

    private fun permissionCheck(){
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
                        val intent = Intent(this@NewsFeedActivity, TrackingActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    //"Permission Denied\n" + tedPermissionResult.deniedPermissions.toString().toast()
                }
            }, { })
    }






    override fun onStop() {
        super.onStop()
        offProgress()
    }

}
