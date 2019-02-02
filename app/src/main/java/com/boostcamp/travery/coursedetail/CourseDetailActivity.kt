package com.boostcamp.travery.coursedetail


import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ActivityCourseDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_course_detail.*


class CourseDetailActivity : BaseActivity<ActivityCourseDetailBinding>(), OnMapReadyCallback {
    private val compositeDisposable = CompositeDisposable()
    override val layoutResourceId: Int = R.layout.activity_course_detail
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(CourseDetailViewModel::class.java)
    }

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewDataBinding.root)
        val mapFragment = fragment_map as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupBindings(savedInstanceState)
    }

    private fun setupBindings(savedInstanceState: Bundle?) {
        //저장된 것이 없다면 새로 생성된 것으로 간주
        //intent로 받은 course를 viewmodel로 넘겨 초기화
        savedInstanceState?.let {}
                ?: viewModel.init(intent.getParcelableExtra(Constants.EXTRA_COURSE))
        viewDataBinding.viewModel = viewModel
        viewModel.loadUserAtionList()
        //상단 리사이클러뷰 터치 막기
        rv_useraction_toplist.setOnTouchListener { v, event -> true }
        //화면상 좌 리사이클러뷰 스크롤
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
        //맵이 준비 되면 viewModel의 LiveData를 observe해서 폴리라인을 그려줌.
        viewModel.latLng.observe(this, Observer {
            map.addPolyline(PolylineOptions().addAll(it))
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(it[0], it[it.size - 1]), 0))
        })

    }
}