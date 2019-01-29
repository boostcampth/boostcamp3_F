package com.boostcamp.travery.search.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionListBinding

class UserActionViewHolder(private val binding: ItemUseractionListBinding) : BaseViewHolder(binding) {
    override fun bind(item: Any) {
        if (item is UserAction) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}