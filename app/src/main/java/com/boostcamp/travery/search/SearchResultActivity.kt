package com.boostcamp.travery.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.dummy.UserActionDummyData
import com.boostcamp.travery.search.adapter.UserActionSearchAdapter
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity(), OnItemClickListener {
    private val adapter = UserActionSearchAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        adapter.setItems(UserActionDummyData.getData())

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@SearchResultActivity)
            adapter = this@SearchResultActivity.adapter
        }
    }

    override fun onItemClick(item: Any) {
        if (item is UserAction) {
            Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
