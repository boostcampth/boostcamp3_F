package com.boostcamp.travery.useraction.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivityUserActionDetailBinding
import com.boostcamp.travery.useraction.save.UserActionSaveActivity
import kotlinx.android.synthetic.main.activity_user_action_detail.*

class UserActionDetailActivity : BaseActivity<ActivityUserActionDetailBinding>(), UserActionDetailViewModel.Contract {
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
        viewModel.setContract(this)

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
            R.id.menu_course_delete -> {
                AlertDialog.Builder(this).apply {
                    setMessage(resources.getString(R.string.dialog_message_delete))
                    setPositiveButton(resources.getString(R.string.dialog_positive)) { _, _ ->
                        viewModel.userAction.get()?.let {
                            viewModel.deleteUserAction(it)
                        }
                    }
                    setNegativeButton(resources.getString(R.string.dialog_negative)) { dialog, _ ->
                        dialog.cancel()
                    }
                }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_USERACTION_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val userAction = it.getParcelableExtra<UserAction>(Constants.EXTRA_USER_ACTION)
                    viewModel.init(userAction)
                    piv_action_image.setSelected(0)
                }
            }
        }
    }

    /**
     * UserAction 이 삭제될 경우 호출되는 메서드
     * 뷰모델이 액티비티에게 통지
     */
    override fun deletedUserAction(userAction: UserAction) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constants.EXTRA_USER_ACTION_DATE, userAction.date)
        })
        finish()
    }
}
