package com.boostcamp.travery.coursedetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.dummy.UserActionDummyData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_course_detail.*


class CourseDetailActivity : FragmentActivity(), OnMapReadyCallback {

    private var topDataList = ArrayList<UserAction?>()
    private var leftDataList = ArrayList<UserAction?>()
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)
        val activityList = UserActionDummyData.getData()
        topDataList.addAll(activityList)
        topDataList.add(null)
        topDataList.add(0, null)
        leftDataList.addAll(activityList)
        leftDataList.add(null)
        leftDataList.add(null)
        initView()
    }


    private fun initView() {
        rv_useraction_toplist.layoutManager = LinearLayoutManager(this)
        rv_useraction_toplist.adapter = UserActionTopListAdapter(topDataList)
        //상단 리사이클러뷰 터치 막기
        rv_useraction_toplist.setOnTouchListener { v, event -> true }



        rv_useraction_leftlist.layoutManager = LinearLayoutManager(this)
        rv_useraction_leftlist.adapter = UserActionLeftListAdapter(leftDataList)
        rv_useraction_leftlist.setOnSnapListener { position -> Log.e("Tt", "" + position) }

        rv_useraction_leftlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //화면상 왼쪽 리사이클러뷰를 스크롤시 상단 아이템도 바뀌여야하므로 스크롤
                rv_useraction_toplist.scrollBy(0, dy)
            }
        })
    }

    /**
     * googleMap Callback
     * @param 구글 맵
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


    }
}