package com.boostcamp.travery.search

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchResultViewModel(application: Application) : BaseViewModel(application) {
    val data = ObservableArrayList<UserAction>()

    private var view: View? = null

    private val tempRepository = Injection.provideCourseRepository(application)

    init {
        loadSearchResult()
    }

    interface View {
        fun onItemClick(item: Any)
    }

    fun setView(view: View) {
        this.view = view
    }

    private fun loadSearchResult() {
        addDisposable(tempRepository.getAllUserAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    data.addAll(it)
                })
    }

    fun onItemClick(item: Any) {
        view?.onItemClick(item)
    }
}