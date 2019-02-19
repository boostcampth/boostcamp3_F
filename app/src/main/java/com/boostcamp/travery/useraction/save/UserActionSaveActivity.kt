package com.boostcamp.travery.useraction.save

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivitySaveUserActionBinding
import com.boostcamp.travery.utils.toast
import com.esafirm.imagepicker.features.ImagePicker
import com.google.android.material.chip.Chip
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_save_user_action.*

class UserActionSaveActivity : BaseActivity<ActivitySaveUserActionBinding>(), UserActionSaveViewModel.View {
    override val layoutResourceId: Int
        get() = R.layout.activity_save_user_action

    private lateinit var viewModel: UserActionSaveViewModel

    private var hashTagSwitch = false

    private var editMode = false

    private var location: Location? = null

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        editMode = intent.extras?.getBoolean(Constants.EDIT_MODE, false) ?: false

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = if (editMode) resources.getString(R.string.string_activity_edit_useraction_toolbar)
            else resources.getString(R.string.string_activity_save_useraction_toolbar)
        }

        viewModel = ViewModelProviders.of(this).get(UserActionSaveViewModel::class.java).apply {
            viewDataBinding.viewmodel = this
            setView(this@UserActionSaveActivity)
        }

        initViewModel()
        initAddButtonList()

        if (editMode) {
            // editMode 일 경우, detail 액티비티로부터 받은 데이터 세팅
            viewModel.setUserAction(intent.getParcelableExtra(Constants.EXTRA_USER_ACTION))

            if (viewModel.imageList.isNotEmpty()) {
                buttonSwitch(rv_save_useraction_image_list, btn_image_add, true)
            }

            if (viewModel.getHashTagCount() > 0) {
                buttonSwitch(chip_group, btn_hash_tag_add, true)
            }
        }
    }

    private fun buttonSwitch(view: View, button: View, on: Boolean) {
        if (on) {
            view.visibility = View.VISIBLE
            button.isSelected = true
        } else {
            view.visibility = View.GONE
            button.isSelected = false
        }
    }

    private fun initViewModel() {
        // Chip 생성 후 그룹에 추가
        disposable.add(viewModel.psHashTag.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    createChip(it)
                    et_hashtag.setText("")
                })

        if (!editMode) {
            // 주소 세팅 및 observe
            // 전달 된 위치가 없을 경우(트래킹중이 아닐 경우 현재위치 로딩안되므로)
            location = viewModel.getLastKnownLocation()
            viewModel.setAddress(
                    intent.getDoubleExtra(
                            Constants.EXTRA_LATITUDE, location?.latitude
                            ?: 0.0
                    ),
                    intent.getDoubleExtra(
                            Constants.EXTRA_LONGITUDE, location?.longitude
                            ?: 0.0
                    )
            )
            viewModel.getAddress().observe(this@UserActionSaveActivity, Observer { tv_location_cur.text = it })
        } else {
            val data = intent.extras?.getParcelable<UserAction>(Constants.EXTRA_USER_ACTION)
            viewModel.setAddress(data?.latitude ?: 0.0, data?.longitude ?: 0.0)
        }
    }

    private fun initAddButtonList() {
        btn_location_add.isSelected = true
        btn_location_add.setOnClickListener {
            "위치 검색 추가 예정".toast(this)
        }

        btn_image_add.setOnClickListener {
            ImagePicker.create(this)
                    .folderMode(true)
                    .imageDirectory(resources.getString(R.string.app_name))
                    .toolbarFolderTitle(getString(R.string.string_folder_title))
                    .theme(R.style.ImagePickerTheme)
                    .start()
        }

        btn_hash_tag_add.setOnClickListener {
            et_hashtag.requestFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            if (hashTagSwitch) {
                if (viewModel.getHashTagCount() == 0) {
                    buttonSwitch(chip_group, btn_hash_tag_add, false)
                }
            } else {
                buttonSwitch(chip_group, btn_hash_tag_add, true)
                currentFocus?.let {
                    imm.showSoftInput(it, 0)
                }
            }
            hashTagSwitch = !hashTagSwitch
        }
    }

    private fun createChip(hashTag: String) {
        Chip(this).apply {
            text = hashTag
            isCloseIconVisible = true
            layoutParams = LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = R.dimen.content_margin_top_n_bottom
                bottomMargin = R.dimen.content_margin_top_n_bottom
            }
            setOnCloseIconClickListener {
                chip_group.removeView(this)
                viewModel.removeHashTag((it as Chip).text as String)
                // 해시태그가 모두 사라졌을 때 처리
                if (chip_group.childCount == 1) {
                    hashTagSwitch = false
                    buttonSwitch(chip_group, btn_hash_tag_add, false)
                }
            }
        }.also {
            chip_group.addView(it, chip_group.childCount - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_course_save -> {
            onProgress()

            if (editMode) {
                viewModel.updateUserAction()

                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(Constants.EXTRA_USER_ACTION, viewModel.userAction.get())
                })
            } else {
                with(intent) {
                    viewModel.saveUserAction(
                            getDoubleExtra(Constants.EXTRA_LATITUDE, location?.latitude ?: 0.0),
                            getDoubleExtra(Constants.EXTRA_LONGITUDE, location?.longitude ?: 0.0),
                            getLongExtra(Constants.EXTRA_COURSE_CODE, 0)
                    )
                }

                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(Constants.EXTRA_LATITUDE, intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0))
                    putExtra(Constants.EXTRA_LONGITUDE, intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0))
                })
            }

            getString(R.string.string_activity_user_action_save_success).toast(this)

            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun saveSelectedImage() {
        // nothing
    }

    override fun imageListEmpty() {
        buttonSwitch(rv_save_useraction_image_list, btn_image_add, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            btn_image_add.isSelected = true
            ImagePicker.getImages(data).forEach {
                rv_save_useraction_image_list.apply {
                    visibility = View.VISIBLE
                    scrollToPosition(0)
                }

                viewModel.imageList.add(0, UserActionImage(it.path))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        offProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.dialog_message))
            setPositiveButton(resources.getString(R.string.dialog_positive)) { _, _ ->
                super.onBackPressed()
            }
            setNegativeButton(resources.getString(R.string.dialog_negative)) { dialog, _ ->
                dialog.cancel()
            }
        }.create().show()
    }
}