package com.boostcamp.travery.community

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.Constants.REQUEST_CODE_LOGIN
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        setContentView(viewDataBinding.root)

        viewDataBinding.viewModel?.openLogin?.observe(this, Observer {
            startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_CODE_LOGIN)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_LOGIN -> viewDataBinding.viewModel?.getPreferences()
            }
        }
    }
}
