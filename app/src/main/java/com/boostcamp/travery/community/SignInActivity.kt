package com.boostcamp.travery.community

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivitySignInBinding
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.esafirm.imagepicker.features.ImagePicker
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_sign_in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        setContentView(viewDataBinding.root)

        setSupportActionBar(toolBar_sign as Toolbar)
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
            ImagePicker.getImages(data).forEach {
                GlideApp.with(img_user)
                        .load(it.path)
                        .circleCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                img_user.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(img_user)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
