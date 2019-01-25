package com.boostcamp.travery.main.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.Route
import com.boostcamp.travery.databinding.ItemRouteListBinding

class RouteViewHolder(private val binding: ItemRouteListBinding) :
    BaseViewHolder(binding) {

    override fun bind(item: Any) {
        if (item is Route) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}