package com.boostcamp.travery.useraction.list

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityUserActionFeedBinding
import kotlinx.android.synthetic.main.activity_user_action_feed.*

class UserActionFeedActivity : BaseActivity<ActivityUserActionFeedBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_feed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        setSupportActionBar(toolBar as Toolbar)
        supportActionBar?.title = ""

        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(UserActionListViewModel::class.java)
    }
}
