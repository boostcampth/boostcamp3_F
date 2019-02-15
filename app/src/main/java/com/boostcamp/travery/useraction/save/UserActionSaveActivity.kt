package com.boostcamp.travery.useraction.save

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivitySaveUserActionBinding
import com.boostcamp.travery.utils.toast
import com.esafirm.imagepicker.features.ImagePicker
import com.google.android.material.chip.Chip
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_save_user_action.*


class UserActionSaveActivity : BaseActivity<ActivitySaveUserActionBinding>(), UserActionSaveViewModel.View {
    lateinit var viewModel: UserActionSaveViewModel
    private val compositeDisposable = CompositeDisposable()

    private var filePath: String? = null

    override val layoutResourceId: Int
        get() = R.layout.activity_save_user_action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = ""

        requestPermission()

        viewModel = ViewModelProviders.of(this).get(UserActionSaveViewModel::class.java).apply {
            viewDataBinding.viewmodel = this
            setView(this@UserActionSaveActivity)

            // 해시태그: MutableLiveData<String> 변화가 감지되면, Chip 생성 후 그룹에 추가
            hashTag.observe(this@UserActionSaveActivity, Observer {
                createChip(it)
                et_hashtag.setText("")
            })

            // 주소 세팅 및 observe
            // 전달 된 위치가 없을 경우(트래킹중이 아닐 경우 현재위치 로딩안되므로)
            val defaultLocation = this.getLastKnownLocation()
            setAddress(intent.getDoubleExtra(Constants.EXTRA_LATITUDE, defaultLocation?.latitude
                    ?: 0.0),
                    intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, defaultLocation?.longitude
                            ?: 0.0))
                    .observe(this@UserActionSaveActivity, Observer {
                        tv_location_cur.text = it
                    })
        }

    }

    private fun createChip(hashTag: String) {
        Chip(this).apply {
            text = hashTag
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chip_group.removeView(this as View)
                viewModel.removeHashTag((it as Chip).text as String)
            }
        }.also {
            chip_group.addView(it as View, chip_group.childCount - 1)
        }
    }

    private fun requestPermission() {
        compositeDisposable.add(
                TedRx2Permission.with(this)
                        .setRationaleTitle(getString(R.string.permission_title))
                        .setRationaleMessage(getString(R.string.permission_message_select_image))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .request()
                        .subscribe({ tedPermissionResult ->
                            if (!tedPermissionResult.isGranted) {
                                getString(R.string.permission_denied) + tedPermissionResult.deniedPermissions.toString().toast(this)
                            }
                        }, { }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_course_save -> {
            with(intent) {
                viewModel.saveUserAction(
                        getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0),
                        getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0),
                        getLongExtra(Constants.EXTRA_COURSE_CODE, 0)
                )

            }
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(Constants.EXTRA_LATITUDE, intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0))
                putExtra(Constants.EXTRA_LONGITUDE, intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0))
            })
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun saveSelectedImage() {
        ImagePicker.create(this)
                .imageDirectory(filePath)
                .folderMode(true)
                .toolbarFolderTitle(getString(R.string.string_folder_title))
                .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            ImagePicker.getImages(data).forEach {
                rv_save_useraction_image_list.scrollToPosition(viewModel.imageList.size)

                viewModel.imageList.add(viewModel.imageList.size - 1, UserActionImage(it.path))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}