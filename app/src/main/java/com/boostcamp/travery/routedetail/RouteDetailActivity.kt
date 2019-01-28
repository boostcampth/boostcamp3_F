package com.boostcamp.travery.routedetail

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.dummy.ActivityDummyData
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import kotlinx.android.synthetic.main.activity_routedetail.*
import kotlinx.android.synthetic.main.activity_routedetail.view.*


class RouteDetailActivity : AppCompatActivity() {
    private var topDataList = ArrayList<Activity?>()
    private var leftDataList = ArrayList<Activity?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routedetail)
        val activityList = ActivityDummyData.getData()
        topDataList.addAll(activityList)
        topDataList.add(null)
        topDataList.add(0, null)
        leftDataList.addAll(activityList)
        leftDataList.add(null)
        leftDataList.add(null)
        initView()
    }


    private fun initView() {
        rv_activity_list.layoutManager = LinearLayoutManager(this)
        GravitySnapHelper(Gravity.TOP).attachToRecyclerView(rv_activity_list)
        rv_activity_list.adapter = ActivityListAdapter(topDataList)

        rv_activity_leftlist.layoutManager = LinearLayoutManager(this)
        var temp = GravitySnapHelper(Gravity.TOP)
        temp.attachToRecyclerView(rv_activity_leftlist)
        rv_activity_leftlist.adapter = ActivityLeftListAdapter(leftDataList)
        var count: Int = 0
        rv_activity_leftlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.e("EEE", "" + dy)
                count += dy
                rv_activity_list.scrollBy(0, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.e("end", "" + count)
                    count = 0
                }
            }
        })
    }
}