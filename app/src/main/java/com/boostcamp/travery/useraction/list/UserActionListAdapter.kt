package com.boostcamp.travery.useraction.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemFeedUserActionBinding
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_feed_user_action.view.*

class UserActionListAdapter(data: ObservableArrayList<UserAction>) : ObservableRecyclerViewAdapter<UserAction, BaseViewHolder>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return UserActionFeedViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_feed_user_action,
                parent,
                false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position))

        val chipGroup = holder.itemView.chip_group_feed

        if (chipGroup.childCount > 0) {
            chipGroup.removeAllViews()
        }
        getItem(position).hashTag.split(' ').forEach { hashTag ->
            if (!hashTag.isEmpty()) {
                Chip(chipGroup.context).apply {
                    text = hashTag
                    isClickable = true
                    isChipIconVisible = false
                    isCheckedIconVisible = false
                }.also {
                    chipGroup.addView(it)
                }
            }
        }
    }
}

class UserActionFeedViewHolder(private val binding: ItemFeedUserActionBinding) : BaseViewHolder(binding) {
    override fun bind(item: Any) {
        if (item is UserAction) {
            binding.apply {
                this.item = item
                executePendingBindings()
            }
        }
    }
}