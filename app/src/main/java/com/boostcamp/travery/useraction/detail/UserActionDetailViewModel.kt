package com.boostcamp.travery.useraction.detail

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.utils.DateUtils
import com.google.android.libraries.places.internal.it
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class UserActionDetailViewModel(application: Application) : BaseViewModel(application) {
    private val repository = Injection.provideCourseRepository(application)

    var userAction = ObservableField<UserAction>()
    val hashTagList = ObservableArrayList<String>()
    val imageList = ObservableArrayList<String>()
    val today = ObservableField<String>()

    private var contract: Contract? = null

    interface Contract {
        fun deletedUserAction(userAction: UserAction)
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    fun init(userAction: UserAction) {
        this.userAction.set(userAction)

        imageList.clear()
        val jsonList = JSONArray(userAction.subImage)
        for (i in 0 until jsonList.length()) {
            imageList.add(jsonList[i].toString())
        }

        hashTagList.clear()
        if (!userAction.hashTag.isEmpty()) {
            hashTagList.addAll(parseHashTag(userAction.hashTag))
        }
    }

    private fun parseHashTag(list: String): List<String> {
        return list.split(" ").map { if (it.startsWith('#')) it else "#$it" }
    }

    fun deleteUserAction() {
        userAction.get()?.let { user ->
            addDisposable(repository.deleteUserAction(user)
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        EventBus.sendEvent(UserActionDeleteEvent(user))
                    })
            contract?.deletedUserAction(user)
        }
        userAction.set(null)
    }
}

data class UserActionDeleteEvent(val userAction: UserAction)