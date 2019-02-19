package com.boostcamp.travery.mapservice.savecourse

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityCourseSaveBinding
import com.boostcamp.travery.utils.toast
import kotlinx.android.synthetic.main.activity_course_save.*

class CourseSaveActivity : BaseActivity<ActivityCourseSaveBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_course_save

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewDataBinding.root)
        setSupportActionBar(toolbar as Toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = resources.getString(R.string.string_activity_save_course_toolbar)
        }

        viewDataBinding.savevm = ViewModelProviders.of(this).get(CourseSaveViewModel::class.java)
        viewDataBinding.savevm?.generateStaticMap(intent.extras)

        initView()
    }

    private fun initView() {
        ArrayAdapter.createFromResource(
                this,
                R.array.theme_array,
                android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_theme.adapter = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_course_save -> {
            viewDataBinding.savevm?.saveCourseToDatabase(intent.extras)
            getString(R.string.string_activity_course_save_success).toast(this)
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
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
