package com.boostcamp.travery.search

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchResultViewModel(application: Application) : BaseViewModel(application) {
    val data = ObservableArrayList<UserAction>()

    private var contract: Contract? = null

    init {
        loadSearchResult()
    }

    interface Contract {
        fun onItemClick(item: Any)
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    private fun loadSearchResult() {
        repository.getAllUserAction()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                data.addAll(it)
            }.also { addDisposable(it) }
    }

    fun onItemClick(item: Any) {
        contract?.onItemClick(item)
    }
}