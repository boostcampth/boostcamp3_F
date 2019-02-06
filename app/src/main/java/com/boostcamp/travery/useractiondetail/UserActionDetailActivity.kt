package com.boostcamp.travery.useractiondetail

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityUserActionDetailBinding
import kotlinx.android.synthetic.main.activity_user_action_detail.*

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
