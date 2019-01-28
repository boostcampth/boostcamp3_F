package com.boostcamp.travery.mapservice.saveroute

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.boostcamp.travery.R
import com.boostcamp.travery.utils.DateUtils
import kotlinx.android.synthetic.main.activity_route_save.*

class RouteSaveActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_save)
        setSupportActionBar(toolbar.findViewById(R.id.toolBar))

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
        menuInflater.inflate(R.menu.activity_route_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menu_route_save -> {
            Toast.makeText(this, resources.getText(R.string.string_save_menu), Toast.LENGTH_SHORT).show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
