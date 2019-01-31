package com.boostcamp.travery.main

import android.app.Application
import android.util.Log
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDbHelper
import com.boostcamp.travery.data.local.prefs.PreferHelper
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.remote.ApiHelper
import com.boostcamp.travery.dummy.CourseDummyData
import com.boostcamp.travery.main.adapter.CourseListAdapter
import com.boostcamp.travery.main.viewholder.GroupItem
import com.boostcamp.travery.utils.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : BaseViewModel(application), OnItemClickListener {
    val adapter = CourseListAdapter(this)

    private var contract: Contract? = null

    interface Contract {
        fun onItemClick(item: Any)
    }

    fun setViewModelContract(contract: Contract) {
        this.contract = contract
    }

    fun loadCourseList() {
//        repository.insertDummyData()
        repository.getAllCourse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map {
                    createGroup(it)
                }.subscribe(
                        {
                            adapter.setItems(it)
                        },
                        {
                            Log.e("TAG", "List load error", it)
                        }
                ).also { addDisposable(it) }
    }

    private fun createGroup(list: List<Course>): List<Any> {
        val result = ArrayList<Any>()
        var partition = -1

        list.forEach { route ->
            if (partition != DateUtils.getTermDay(toMillis = route.endTime)) {
                result.add(GroupItem("${DateUtils.getDate(route.endTime)[2]}"))
            }
            partition = DateUtils.getTermDay(toMillis = route.endTime)
            result.add(route)
        }

        return result
    }

    override fun onItemClick(item: Any) {
        contract?.onItemClick(item)
    }
}