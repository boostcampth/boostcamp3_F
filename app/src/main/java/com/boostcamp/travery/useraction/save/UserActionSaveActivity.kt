package com.boostcamp.travery.useraction.save

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ActivitySaveUserActionBinding
import com.boostcamp.travery.utils.toast
import com.esafirm.imagepicker.features.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_save_user_action.*

class UserActionSaveActivity : BaseActivity<ActivitySaveUserActionBinding>(), UserActionSaveViewModel.View {
    override val layoutResourceId: Int
        get() = R.layout.activity_save_user_action

    private lateinit var viewModel: UserActionSaveViewModel
    private val disposable = CompositeDisposable()

    private var editMode = false
    private var singleMode = false

    private lateinit var receivedData: UserAction

    private var location: LatLng? = null

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        setToolbar()
        initViewModel()
        setMode()
        initUserAction()
        initAddButtonList()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = if (editMode) resources.getString(R.string.string_activity_edit_useraction_toolbar)
            else resources.getString(R.string.string_activity_save_useraction_toolbar)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(UserActionSaveViewModel::class.java).apply {
            viewDataBinding.viewmodel = this
        }

        // Chip 생성 후 그룹에 추가
        disposable.add(viewModel.psHashTag.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    createChip(it)
                    et_hashtag.setText("")
                })

        viewModel.setView(this)
    }

    private fun setMode() {
        editMode = intent.extras?.getBoolean(Constants.EDIT_MODE, false) ?: false
        singleMode = intent.extras?.getBoolean(Constants.SINGLE_ADD_USER_ACTION_MODE, false)
                ?: false
    }

    private fun initUserAction() {
        val location = viewModel.getLocation() ?: LatLng(0.0, 0.0)
        val latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, location.latitude)
        val longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, location.longitude)
        val courseCode = intent.getLongExtra(Constants.EXTRA_COURSE_CODE, 0)

        receivedData = intent.getParcelableExtra(Constants.EXTRA_USER_ACTION) ?: UserAction(
                latitude = latitude,
                longitude = longitude,
                courseCode = courseCode
        )
        viewModel.setUserAction(receivedData)
    }

    private fun initAddButtonList() {
        btn_location_add.isSelected = true
        btn_location_add.setOnClickListener {
            selectLocation()
        }

        // 현재 주소를 클릭했을 경우에도 위치를 선택할 수 있도록 추가
        tv_location_cur.setOnClickListener {
            selectLocation()
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
            chip_group.visibility = if (chip_group.visibility == View.VISIBLE && chip_group.childCount < 1) View.GONE else View.VISIBLE
            et_hashtag.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            currentFocus?.let {
                imm.showSoftInput(it, 0)
            }
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
            }
        }.also {
            chip_group.addView(it, chip_group.childCount - 1)
        }
    }

    private fun selectLocation() {
        if (singleMode) {
            val intent = Intent(this, FindLocationActivity::class.java).apply {
                putExtra(Constants.EXTRA_LAT_LNG, location)
            }
            startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_LOCATION)
        } else {
            getString(R.string.string_toast_warning_message_location_select).toast(this)
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
            } else {
                viewModel.saveUserAction()
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
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

        if (requestCode == Constants.REQUEST_CODE_SELECT_LOCATION && resultCode == Activity.RESULT_OK) {
            val extra = data?.getParcelableExtra<LatLng>(Constants.EXTRA_LAT_LNG)
            extra?.run {
                viewModel.setAddress(latitude, longitude)
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

    override fun onSaveUserAction(userAction: UserAction?) {
        userAction?.let {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(Constants.EXTRA_USER_ACTION, it)
            })
        }
        handler.post {
            getString(R.string.string_activity_user_action_save_success).toast(this)
        }
        finish()
    }
}