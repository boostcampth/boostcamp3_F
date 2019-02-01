package com.boostcamp.travery.save

import android.os.Bundle
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivitySaveUserActionBinding

class UserActionSaveActivity : BaseActivity<ActivitySaveUserActionBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_save_user_action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
    }
}
