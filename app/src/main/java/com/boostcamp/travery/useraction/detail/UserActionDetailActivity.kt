package com.boostcamp.travery.useraction.detail

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityUserActionDetailBinding

class UserActionDetailActivity : BaseActivity<ActivityUserActionDetailBinding>() {
    lateinit var viewModel: UserActionDetailViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        viewModel = ViewModelProviders.of(this).get(UserActionDetailViewModel::class.java)
        viewDataBinding.viewmodel = viewModel

        viewModel.init(intent.getParcelableExtra(Constants.EXTRA_USER_ACTION))
    }
}
