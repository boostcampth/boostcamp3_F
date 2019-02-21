package com.boostcamp.travery.feed

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.NewsFeedRepository
import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.model.NewsFeed

class NewsFeedViewModel(application: Application) : BaseViewModel(application) {
    private val newsFeedRepository = NewsFeedRepository.getInstance()
    private val feedList = ObservableArrayList<NewsFeed>()
    private var start = 0
    val isLoding = MutableLiveData<Boolean>()

    init {
        loadFeedList()
    }

    fun loadFeedList() {
        isLoding.value = true
        addDisposable(newsFeedRepository.getFeedList(start)
                .subscribe({
                    feedList.addAll(it)
                    start += it.size
                    isLoding.value = false
                }, {
                    isLoding.value = false
                })
        )
    }

    fun refreshList() {
        start = 0
        feedList.clear()
        isLoding.value = true
        addDisposable(newsFeedRepository.reloadList()
                .subscribe({
                    feedList.addAll(it)
                    start += it.size
                    isLoding.value = false
                }, {
                    isLoding.value = false
                })
        )
    }

    fun getFeedList(): ObservableArrayList<NewsFeed> {
        return this.feedList
    }


    fun getPreferences(): User {
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_LOGIN, Context.MODE_PRIVATE)
        return User(nickname = pref.getString(Constants.PREF_USER_NAME, getApplication<android.app.Application>().getString(R.string.string_setting_request_login))!!,
                image = pref.getString(Constants.PREF_USER_IMAGE, "")!!)
    }
}