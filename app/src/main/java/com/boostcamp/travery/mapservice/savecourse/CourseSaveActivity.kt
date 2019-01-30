package com.boostcamp.travery.mapservice.savecourse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.main.MainActivity
import com.boostcamp.travery.utils.DateUtils
import kotlinx.android.synthetic.main.activity_course_save.*

class CourseSaveActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_save)
        setSupportActionBar(toolbar as Toolbar)
        title = ""

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

    private fun saveCourseToDatabase() {
        val imageFilePath = requestStaticMap() // 비동기 호출 예상

        // DB 저장 코드
//        with(intent) {
//            Thread(Runnable {
//                AppDatabase.getDataBase(this@CourseSaveActivity)
//                        .daoCourse()
//                        .insertCourse(Course(
//                                et_title.text.toString(),
//                                et_content.text.toString(),
//                                et_selected_theme.text.toString(),
//                                getLongExtra(Constants.EXTRA_ROUTE_START_TIME, System.currentTimeMillis()),
//                                getLongExtra(Constants.EXTRA_ROUTE_END_TIME, System.currentTimeMillis()),
//                                getLongExtra(Constants.EXTRA_ROUTE_DISTANCE, 0L),
//                                getStringExtra(Constants.EXTRA_ROUTE_COORDINATE),
//                                imageFilePath))
//            }).start()
//        }
    }

    private fun requestStaticMap(): String {
        // 스태틱맵을 요청하여 이미지 파일 저장 후 저장 경로 리턴
        return ""
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
            saveCourseToDatabase()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
