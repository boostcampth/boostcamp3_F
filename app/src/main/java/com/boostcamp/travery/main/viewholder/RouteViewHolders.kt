package com.boostcamp.travery.main.viewholder

import com.boostcamp.travery.data.model.Route
import com.boostcamp.travery.databinding.ItemRouteListBinding
import com.boostcamp.travery.databinding.ItemRouteListGroupBinding
import com.boostcamp.travery.main.adapter.GroupItem

class RouteViewHolder(private val binding: ItemRouteListBinding) :
    BaseViewHolder(binding) {

    override fun bind(item: Any) {
        if (item is Route) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}

class RouteGroupViewHolder(private val binding: ItemRouteListGroupBinding) :
    BaseViewHolder(binding) {

    override fun bind(item: Any) {
        if (item is GroupItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}