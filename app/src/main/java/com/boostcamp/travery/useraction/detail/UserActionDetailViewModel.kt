package com.boostcamp.travery.useraction.detail

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.utils.DateUtils
import org.json.JSONArray


class UserActionDetailViewModel(application: Application) : BaseViewModel(application) {
    var userAction = ObservableField<UserAction>()
    val hashTagList = ObservableArrayList<String>()
    val today = ObservableField<String>()

    val imageList = ObservableArrayList<String>()

    fun init(userAction: UserAction) {
        this.userAction.set(userAction.also { today.set(DateUtils.parseDateAsString(it.date, "yyyy.MM.dd aa HH:mm")) })

        val imageList = ArrayList<String>()

        val jsonList = JSONArray(userAction.subImage)
        for (i in 0 until jsonList.length()) {
            imageList.add(jsonList[i].toString())
        }

        this.imageList.clear()
        this.imageList.addAll(imageList)

        hashTagList.clear()
        if (!userAction.hashTag.isEmpty()) {
            hashTagList.addAll(parseHashTag(userAction.hashTag))
        }
    }

    private fun parseHashTag(list: String): List<String> {
        return list.split(" ").map { if (it.startsWith('#')) it else "#$it"}
    }
}