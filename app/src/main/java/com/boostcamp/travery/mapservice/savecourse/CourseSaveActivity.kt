package com.boostcamp.travery.mapservice.savecourse

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityCourseSaveBinding
import com.boostcamp.travery.utils.DateUtils
import kotlinx.android.synthetic.main.activity_course_save.*

class CourseSaveActivity : BaseActivity() {

    lateinit var binding: ActivityCourseSaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_save)
        setSupportActionBar(toolbar as Toolbar)
        title = ""
        binding.savevm = ViewModelProviders.of(this).get(CourseSaveViewModel::class.java)
        binding.setLifecycleOwner(this)

        initView()
    }

    private fun initView() {
        tv_date_cur.text = DateUtils.getDateToString()

        ArrayAdapter.createFromResource(
            this,
            R.array.theme_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_theme.adapter = it
        }

        //spinner_theme.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_course_save -> {
            binding.savevm?.saveCourseToDatabase(intent.extras)
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
