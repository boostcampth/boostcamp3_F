package com.boostcamp.travery.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.dummy.ActivityDummyData
import com.boostcamp.travery.search.adapter.ActivitySearchAdapter
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity(), OnItemClickListener {
    private val adapter = ActivitySearchAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        adapter.setItems(ActivityDummyData.getData())

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SearchResultActivity)
            adapter = this@SearchResultActivity.adapter
        }
    }

    override fun onItemClick(item: Any) {
        if (item is Activity) {
            Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
