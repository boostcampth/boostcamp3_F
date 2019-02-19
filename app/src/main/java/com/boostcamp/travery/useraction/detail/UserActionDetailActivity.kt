package com.boostcamp.travery.useraction.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSnapHelper
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityUserActionDetailBinding
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import kotlinx.android.synthetic.main.activity_user_action_detail.*

class UserActionDetailActivity : BaseActivity<ActivityUserActionDetailBinding>() {
    lateinit var viewModel: UserActionDetailViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_user_action_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            title = getString(R.string.string_activity_title_action_detail)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModel = ViewModelProviders.of(this).get(UserActionDetailViewModel::class.java)
        viewDataBinding.viewmodel = viewModel

        viewModel.init(intent.getParcelableExtra(Constants.EXTRA_USER_ACTION))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_save_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_course_edit -> {
                startActivityForResult(
                        Intent(this, UserActionSaveActivity::class.java).apply {
                            putExtra(Constants.EXTRA_USER_ACTION, viewModel.userAction.get())
                            putExtra(Constants.EDIT_MODE, true)
                        }, Constants.REQUEST_CODE_USERACTION_EDIT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_USERACTION_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let { viewModel.init(it.getParcelableExtra(Constants.EXTRA_USER_ACTION)) }
            }
        }
    }
}
