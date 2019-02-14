package com.boostcamp.travery.useraction.detail

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import java.util.regex.Pattern

class UserActionDetailViewModel(application: Application) : BaseViewModel(application) {
    var userAction: UserAction? = null
    val imageList = ObservableArrayList<String>()
    val hashTagList = ArrayList<String>()

    fun init(userAction: UserAction) {
        this.userAction = userAction

        imageList.add(userAction.mainImage)
        imageList.addAll(parseImageList(userAction.subImage))
        hashTagList.addAll(parseHashTag(userAction.hashTag))
    }

    private fun parseImageList(list: String): List<String> {
        return list.split(",")
    }

    private fun parseHashTag(list: String): List<String> {
        val result = ArrayList<String>()
        list.split(" ").forEach {
            if (Pattern.matches("^#[0-9a-zA-Z가-힣]*$", it)) result.add(it)
        }
        return result
    }

    fun onItemClick(item: Any) {
        if (item is String) {

        }
    }
}