package com.boostcamp.travery.feed

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityNewsfeedBinding
import kotlinx.android.synthetic.main.activity_newsfeed.*

class NewsFeedActivity : BaseActivity<ActivityNewsfeedBinding>() {
    override val layoutResourceId: Int = R.layout.activity_newsfeed
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(NewsFeedViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)


        viewDataBinding.viewModel = viewModel
        viewDataBinding.setLifecycleOwner(this)

        //refreshListner onRefresh
        sl_feed.setOnRefreshListener {
            viewModel.refreshList()
        }
        viewModel.isLoding.observe(this, Observer {
            sl_feed.isRefreshing = it
        })

        initView()
        rv_newsfeed_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                if (totalItemCount == (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()) {
                    viewModel.loadFeedList()
                }
            }
        })
    }

    private fun initView() {
        rv_newsfeed_list.layoutManager = LinearLayoutManager(this)
        rv_newsfeed_list.adapter = NewsFeedListAdapter(viewModel.getFeedList())
    }

}
