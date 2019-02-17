package com.boostcamp.travery.feed

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.NewsFeedRepository
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
}