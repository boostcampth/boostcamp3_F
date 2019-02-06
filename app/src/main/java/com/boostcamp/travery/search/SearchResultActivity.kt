package com.boostcamp.travery.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.boostcamp.travery.Constants
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivitySearchResultBinding
import com.boostcamp.travery.search.adapter.UserActionSearchAdapter
import com.boostcamp.travery.useractiondetail.UserActionDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>(), OnItemClickListener {
    private val adapter = UserActionSearchAdapter(this)

    override val layoutResourceId: Int
        get() = R.layout.activity_search_result

    // 뷰모델로 분리 필요
    private val db = AppDbHelper.getInstance(this)
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        db.getAllUserAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.setItems(it)
                }.also {
                    compositeDisposable.add(it)
                }

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
        if (item is UserAction) {
            startActivity(Intent(this, UserActionDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_USER_ACTION, item)
            })
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
