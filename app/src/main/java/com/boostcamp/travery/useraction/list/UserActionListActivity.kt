package com.boostcamp.travery.useraction.list

import android.os.Bundle
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityUserActionListBinding

class UserActionListActivity : BaseActivity<ActivityUserActionListBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
    }
}
