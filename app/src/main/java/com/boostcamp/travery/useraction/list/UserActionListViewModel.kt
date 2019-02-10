package com.boostcamp.travery.useraction.list

import android.app.Application
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserActionListViewModel(application: Application) : BaseViewModel(application) {
    private var contract: Contract? = null

    init {
        loadUserActions()
    }

    interface Contract {
        fun onUserActionLoading(list: List<UserAction>)
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    private fun loadUserActions() {
        repository.getAllUserAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    contract?.onUserActionLoading(it)
                }.also { addDisposable(it) }
    }
}