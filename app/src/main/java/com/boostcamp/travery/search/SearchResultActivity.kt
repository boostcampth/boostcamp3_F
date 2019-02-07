package com.boostcamp.travery.search

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boostcamp.travery.Constants
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivitySearchResultBinding
import com.boostcamp.travery.useractiondetail.UserActionDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>(), SearchResultViewModel.Contract {

    override val layoutResourceId: Int
        get() = R.layout.activity_search_result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(SearchResultViewModel::class.java).apply {
            setContract(this@SearchResultActivity)
        }
    }

    override fun onItemClick(item: Any) {
        if (item is UserAction) {
            startActivity(Intent(this, UserActionDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_USER_ACTION, item)
            })
        }
    }
}
