package com.boostcamp.travery.presentation.course.list

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.Injection
import com.boostcamp.travery.MyApplication
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.presentation.course.list.adapter.viewholder.GroupItem
import com.boostcamp.travery.presentation.course.save.CourseSaveEvent
import com.boostcamp.travery.utils.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CourseListViewModel(application: Application) : BaseViewModel(application) {
    val data = ObservableArrayList<Any>()

    private val mainRepository = Injection.provideCourseRepository(application)

    init {
        loadCourseList()
        addDisposable(EventBus.getEvents().ofType(CourseSaveEvent::class.java).subscribe {
            addCourseItem(it.course)
        })
    }

    private var view: View? = null

    interface View {
        fun onItemClick(item: Any)
    }

    fun setView(view: View) {
        this.view = view
    }

    private fun loadCourseList() {
        mainRepository.getAllCourse()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map {
                createGroup(it)
            }.subscribe(
                {
                    data.addAll(it)
                },
                {
                    Log.e("TAG", "List load error", it)
                }
            ).also { addDisposable(it) }
    }

    // 그룹 타이틀 삽입
    private fun createGroup(list: List<Course>): List<Any> {
        val result = ArrayList<Any>()
        var partition = -1

        list.forEach { course ->
            val curTime = course.endTime

            if (partition != DateUtils.getTermDay(toMillis = curTime)) {
                val termDay = DateUtils.getTermDay(toMillis = curTime)
                result.add(
                    GroupItem(
                        curTime,
                        when (termDay) {
                            0 -> getResources().getString(R.string.string_group_title_today)
                            1 -> getResources().getString(R.string.string_group_title_yesterday)
                            else -> DateUtils.parseDateAsString(curTime, "yyyy.MM.dd EEE")
                        }
                    )
                )
            }
            partition = DateUtils.getTermDay(toMillis = curTime)
            result.add(course)
        }

        return result
    }

    private fun getResources(): Resources = getApplication<MyApplication>().resources

    /**
     * 리스트에서 그룹 아이템 제거
     */
    private fun removeGroup(list: List<Any>): MutableList<Course> {
        return list.mapNotNull {
            it as? Course
        }.toMutableList()
    }

    private fun addCourseItem(course: Course) {
        val list = removeGroup(data)
        list.add(0, course)
        data.clear()
        data.addAll(createGroup(list))
    }

    fun onItemClick(item: Any) {
        view?.onItemClick(item)
    }
}