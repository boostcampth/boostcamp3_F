package com.boostcamp.travery.main.adapter

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.databinding.ItemRouteListBinding
import com.boostcamp.travery.databinding.ItemRouteListGroupBinding
import com.boostcamp.travery.main.viewholder.GroupItem
import com.boostcamp.travery.main.viewholder.GroupViewHolder
import com.boostcamp.travery.main.viewholder.RouteViewHolder

class RouteListAdapter(private var listener: OnItemClickListener) : BaseAdapter<Any>() {

    companion object {
        private const val VIEW_TYPE_GROUP = 1
        private const val VIEW_TYPE_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = DataBindingUtil.inflate<ItemRouteListGroupBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_route_list_group,
                        parent,
                        false)
                GroupViewHolder(binding)
            }

            else -> {
                val binding = DataBindingUtil.inflate<ItemRouteListBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_route_list,
                        parent,
                        false)
                binding.listener = this@RouteListAdapter.listener
                RouteViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is GroupItem) VIEW_TYPE_GROUP
        else VIEW_TYPE_ITEM
    }
}