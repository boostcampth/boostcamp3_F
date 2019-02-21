package com.boostcamp.travery.feed

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Constants
import com.boostcamp.travery.Injection
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.NewsFeedRepository
import com.boostcamp.travery.data.model.Bar
import com.boostcamp.travery.data.model.BaseItem
import com.boostcamp.travery.data.model.Guide
import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.utils.DateUtils
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class NewsFeedViewModel(application: Application) : BaseViewModel(application) {
    private val newsFeedRepository = NewsFeedRepository.getInstance()
    private val courseRepository = Injection.provideCourseRepository(application)
    private val feedList = ObservableArrayList<BaseItem>()
    private var start = 0
    val isLoading = MutableLiveData<Boolean>()
    var isLast = false

    init {
        refreshList()
    }

    fun loadFeedList() {
        isLoading.value = true
        addDisposable(newsFeedRepository.getFeedList(start)
                .subscribe({
                    if (it.size < 10) {
                        isLast = true
                    }
                    feedList.addAll(it)
                    start += it.size
                    isLoading.value = false
                }, {
                    isLoading.value = false
                })
        )
    }

    fun refreshList() {
        start = 0
        feedList.clear()
        isLoading.value = true
        isLast = false

        if (showGuideLine()) {
            val array = JSONArray()
            array.put(Uri.parse("android.resource://${R::class.java.getPackage()?.name}/${R.drawable.t1}").toString())
            array.put(Uri.parse("android.resource://${R::class.java.getPackage()?.name}/${R.drawable.t2}").toString())
            feedList.add(Guide(array.toString()))
        }

        addDisposable(Flowable.merge(Flowable.just(Bar(Constants.TYPE_TOP_BAR, "오늘의 흔적")),
                courseRepository.getTodayCourse(DateUtils.getToday()).flatMap { list ->
                    if (list.isNotEmpty()) {
                        Flowable.fromIterable(list)
                    } else {
                        Flowable.just(Bar(Constants.TYPE_MIDDLE_BAR, "아직 오늘의 기록이 없습니다."))
                    }
                }, Flowable.just(Bar(Constants.TYPE_BOTTOM_BAR, "")), newsFeedRepository.reloadList().toFlowable()
                .flatMap { list ->
                    if (list.size < 10) {
                        isLast = true
                    }
                    start += list.size
                    Flowable.fromIterable(list)
                }).toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    feedList.addAll(it)
                    isLoading.value = false
                }, {
                    isLoading.value = false
                })
        )

    }

    fun getFeedList(): ObservableArrayList<BaseItem> {
        return this.feedList
    }


    fun getPreferences(): User {
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_LOGIN, Context.MODE_PRIVATE)
        val user = User()
        user.nickname = pref.getString(Constants.PREF_USER_NAME, getApplication<android.app.Application>().getString(com.boostcamp.travery.R.string.string_setting_request_login))
        user.image = pref.getString(Constants.PREF_USER_IMAGE, "")
        return user
    }

    fun setInvisibleGuideLine() {
        feedList.removeAt(0)
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_GUIDE, Context.MODE_PRIVATE)
        val edit = pref.edit()
        edit.putBoolean(Constants.PREF_GUIDE_SHOW, false)
        edit.apply()
    }

    private fun showGuideLine(): Boolean {
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_GUIDE, Context.MODE_PRIVATE)
        return pref.getBoolean(Constants.PREF_GUIDE_SHOW, true)
    }
}