package com.boostcamp.travery.routedetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.dummy.ActivityDummyData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_routedetail.*


class RouteDetailActivity : FragmentActivity(),OnMapReadyCallback {

    private var topDataList = ArrayList<Activity?>()
    private var leftDataList = ArrayList<Activity?>()
    private var map:GoogleMap?=null
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
        rv_activity_toplist.layoutManager = LinearLayoutManager(this)
        rv_activity_toplist.adapter = ActivityTopListAdapter(topDataList)
        //상단 리사이클러뷰 터치 막기
        rv_activity_toplist.setOnTouchListener { v, event -> true }



        rv_activity_leftlist.layoutManager = LinearLayoutManager(this)
        rv_activity_leftlist.adapter = ActivityLeftListAdapter(leftDataList)
        rv_activity_leftlist.setOnSnapListener { position -> Log.e("Tt", "" + position) }

        rv_activity_leftlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //화면상 왼쪽 리사이클러뷰를 스크롤시 상단 아이템도 바뀌여야하므로 스크롤
                rv_activity_toplist.scrollBy(0, dy)
            }
        })
    }

    /**
     * googleMap Callback
     * @param 구글 맵
     *
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        map=googleMap


    }
}