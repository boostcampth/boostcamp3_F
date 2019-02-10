package com.boostcamp.travery.main.adapter.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.databinding.ItemCourseListGroupBinding

class GroupViewHolder(private val binding: ItemCourseListGroupBinding) : BaseViewHolder(binding) {
    override fun bind(item: Any) {
        if (item is GroupItem) {
            binding.item = item
        }
    }
}

data class GroupItem(val date: Long, val title: String)