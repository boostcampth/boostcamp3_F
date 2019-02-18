package com.boostcamp.travery.useraction.detail

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import org.json.JSONArray


class UserActionDetailViewModel(application: Application) : BaseViewModel(application) {
    var userAction: UserAction? = null
    val imageList = ObservableArrayList<String>()
    val hashTagList = ArrayList<String>()

    fun init(userAction: UserAction) {
        this.userAction = userAction

        val jsonList = JSONArray(userAction.subImage)
        for (i in 0 until jsonList.length()) {
            imageList.add(jsonList[i].toString())
        }

        hashTagList.addAll(parseHashTag(userAction.hashTag))
    }

    private fun parseHashTag(list: String): List<String> {
        return list.split(" ")
    }

    fun onItemClick(item: Any) {
        if (item is String) {

        }
    }
}