package com.boostcamp.travery.useraction.detail

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSnapHelper
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityUserActionDetailBinding
import kotlinx.android.synthetic.main.activity_user_action_detail.*

class UserActionDetailActivity : BaseActivity<ActivityUserActionDetailBinding>() {
    lateinit var viewModel: UserActionDetailViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        setSupportActionBar(toolBar as Toolbar)
        supportActionBar?.title = ""

        viewModel = ViewModelProviders.of(this).get(UserActionDetailViewModel::class.java)
        viewDataBinding.viewmodel = viewModel

        viewModel.init(intent.getParcelableExtra(Constants.EXTRA_USER_ACTION))

        LinearSnapHelper().attachToRecyclerView(rv_image_list)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_save_edit_menu, menu)
        return true
    }
}
