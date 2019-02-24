package com.boostcamp.travery.presentation.useraction.list

import android.app.Application
import android.location.Location
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserActionListViewModel(application: Application) : BaseViewModel(application) {
    private val curLocation = MutableLiveData<Location>()
    fun getCurLocation() = curLocation

    val mUserActionList = ObservableArrayList<UserAction>()
    private val userActionList = MutableLiveData<List<UserAction>>()
    fun getUserActionList() = userActionList

    private val userActionRepository = Injection.provideCourseRepository(application)

    init {
        loadUserActions()
    }

    private fun loadUserActions() {
        addDisposable(
                userActionRepository.getAllUserAction()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            userActionList.value = it

                            mUserActionList.clear()
                            mUserActionList.addAll(it)
                        })
    }

    fun setCurrentLocation() {
        getLastKnownLocation()?.let {
            curLocation.value = it
        }
    }
}