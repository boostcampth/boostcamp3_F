package com.boostcamp.travery.search

import android.app.Application
import android.text.Html
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SearchResultViewModel(application: Application) : BaseViewModel(application) {
    val data = MutableLiveData<List<UserAction>>()
    private var preQuery: String? = null
    private val subject = PublishSubject.create<String>()
    val isView = ObservableField<Boolean>(false)

    private val courseRepository = Injection.provideCourseRepository(application)

    init {
        addDisposable(subject.debounce(200, TimeUnit.MILLISECONDS, Schedulers.computation())
                .filter { !it.isEmpty() && it == preQuery }
                .flatMap { courseRepository.getUserActionForKeyword(it).toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isEmpty()) {
                        isView.set(true)
                    } else {
                        isView.set(false)
                    }
                    data.value = it
                })
    }


    fun putQuery(query: String) {
        subject.onNext(query)
        preQuery = query
    }


}



