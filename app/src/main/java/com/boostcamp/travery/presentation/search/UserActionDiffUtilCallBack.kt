package com.boostcamp.travery.presentation.search

import androidx.recyclerview.widget.DiffUtil
import com.boostcamp.travery.data.model.UserAction

class UserActionDiffUtilCallBack(private var newList: ArrayList<UserAction>?,
                                 private var oldList: ArrayList<UserAction>?) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.seq == newList?.get(newItemPosition)?.seq
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList?.get(newItemPosition)?.seq == oldList?.get(oldItemPosition)?.seq &&
                newList?.get(newItemPosition)?.title == oldList?.get(oldItemPosition)?.title &&
                newList?.get(newItemPosition)?.body == oldList?.get(oldItemPosition)?.body &&
                newList?.get(newItemPosition)?.date == oldList?.get(oldItemPosition)?.date &&
                newList?.get(newItemPosition)?.hashTag == oldList?.get(oldItemPosition)?.hashTag &&
                newList?.get(newItemPosition)?.mainImage == oldList?.get(oldItemPosition)?.mainImage &&
                newList?.get(newItemPosition)?.subImage == oldList?.get(oldItemPosition)?.subImage &&
                newList?.get(newItemPosition)?.latitude == oldList?.get(oldItemPosition)?.latitude &&
                newList?.get(newItemPosition)?.longitude == oldList?.get(oldItemPosition)?.longitude &&
                newList?.get(newItemPosition)?.courseCode == oldList?.get(oldItemPosition)?.courseCode &&
                newList?.get(newItemPosition)?.address == oldList?.get(oldItemPosition)?.address
    }
}
