package com.boostcamp.travery.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivitySearchResultBinding
import com.boostcamp.travery.useraction.detail.UserActionDetailActivity

class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_search_result

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SearchResultViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        viewDataBinding.viewmodel = viewModel

        initView()
        ovbserveItem()
    }

    private fun initView() {
        viewDataBinding.rvSearchList.layoutManager = LinearLayoutManager(this)
        viewDataBinding.rvSearchList.adapter = SearchResultListAdapter { user ->
            val intent = Intent(this, UserActionDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_USER_ACTION, user)
            }
            startActivity(intent)
        }
        viewDataBinding.rvSearchList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        setSupportActionBar(viewDataBinding.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        viewDataBinding.searchView.onActionViewExpanded()
        viewDataBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("QUERY_submit", query)
                query?.let { viewModel.putQuery(query) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("QUERY_chande", newText)
                newText?.let { viewModel.putQuery(newText) }
                return false
            }
        })
    }

    private fun ovbserveItem() {
        viewModel.data.observe(this, Observer {
            (viewDataBinding.rvSearchList.adapter as SearchResultListAdapter).updateListItems(it as ArrayList<UserAction>)
        })
    }
}
