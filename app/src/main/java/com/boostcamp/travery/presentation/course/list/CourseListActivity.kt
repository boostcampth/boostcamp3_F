package com.boostcamp.travery.presentation.course.list


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.presentation.course.detail.CourseDetailActivity
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ActivityCourseListBinding
import kotlinx.android.synthetic.main.activity_main_feed.*

class CourseListActivity : BaseActivity<ActivityCourseListBinding>(),
        CourseListViewModel.View {


    override val layoutResourceId: Int
        get() = R.layout.activity_course_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewDataBinding.root)

        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(CourseListViewModel::class.java).apply {
            setView(this@CourseListActivity)
        }

        setSupportActionBar(toolBar as Toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(item: Any) {
        if (item is Course) {
            startActivity(Intent(this, CourseDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_COURSE, item)
            })
        }
    }


}
