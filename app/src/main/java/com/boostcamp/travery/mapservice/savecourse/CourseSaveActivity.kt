package com.boostcamp.travery.mapservice.savecourse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.main.MainActivity
import com.boostcamp.travery.databinding.ActivityCourseSaveBinding
import com.boostcamp.travery.databinding.ItemCourseListGroupBinding
import com.boostcamp.travery.utils.DateUtils
import kotlinx.android.synthetic.main.activity_course_save.*

class CourseSaveActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding = DataBindingUtil.setContentView<ActivityCourseSaveBinding>(this, R.layout.activity_course_save)
        setSupportActionBar(toolbar as Toolbar)
        title = ""
        binding.savevm = ViewModelProviders.of(this).get(CourseSaveViewModel::class.java)
        binding.setLifecycleOwner(this)

        //binding = DataBindingUtil.setContentView(this, R.layout.activity_course_save)
        //viewmodel = ViewModelProviders.of(this).get(CourseSaveViewModel::class.java)
        //binding.savevm = viewmodel
        //binding.setLifecycleOwner(this)

        //setContentView(R.layout.activity_course_save)

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

        spinner_theme.onItemSelectedListener = this
    }

    private fun editTextSetting(editText: EditText, check: Boolean) {
        editText.apply {
            isFocusableInTouchMode = check
            isEnabled = check
            isFocusable = check
            isClickable = check
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do-nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val select = parent?.getItemAtPosition(position).toString()
        if (select == resources.getText(R.string.string_input_theme)) {
            editTextSetting(et_selected_theme, true)
            et_selected_theme.apply {
                setText("", TextView.BufferType.EDITABLE)
                hint = resources.getText(R.string.string_input_theme)
            }
        } else {
            editTextSetting(et_selected_theme, false)
            et_selected_theme.setText(select, TextView.BufferType.EDITABLE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_course_save -> {
            //saveCourseToDatabase()
            //binding.savevm?.save()
            //viewmodel.save()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
