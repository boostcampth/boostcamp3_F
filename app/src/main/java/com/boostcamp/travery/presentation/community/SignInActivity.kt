package com.boostcamp.travery.presentation.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivitySignInBinding
import com.esafirm.imagepicker.features.ImagePicker
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_sign_in

    private val view= object :SignInViewModel.View{
        override fun toastMessage(message:String) {
            Toast.makeText(this@SignInActivity,message,Toast.LENGTH_SHORT).show()
        }

        override fun returnResult() {
            setResult(Activity.RESULT_OK)
            finish()
        }

        override fun onLoading() {
            onProgress(resources.getString(R.string.progress_bar_message))
        }

        override fun offLoading() {
            offProgress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java).apply {
            setView(view)
        }
        setContentView(viewDataBinding.root)

        setSupportActionBar(toolBar_sign as Toolbar)

        viewDataBinding.viewModel?.setUserId(intent.getStringExtra(Constants.EXTRA_USER_ID))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = resources.getString(R.string.string_activity_sign_toolbar)
        }

        img_user.setOnClickListener {
            ImagePicker.create(this)
                    .folderMode(true)
                    .single()
                    .imageDirectory(resources.getString(R.string.app_name))
                    .toolbarFolderTitle(getString(R.string.string_folder_title))
                    .theme(R.style.ImagePickerTheme)
                    .start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            viewDataBinding.viewModel?.setUserImage(ImagePicker.getFirstImageOrNull(data).path)
            GlideApp.with(this)
                    .load(ImagePicker.getFirstImageOrNull(data).path)
                    .circleCrop()
                    .into(img_user)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}
