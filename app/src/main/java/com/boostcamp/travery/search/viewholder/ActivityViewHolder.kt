package com.boostcamp.travery.search.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.databinding.ItemActivityListBinding

class ActivityViewHolder(private val binding: ItemActivityListBinding) : BaseViewHolder(binding) {
    override fun bind(item: Any) {
        if (item is Activity) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}