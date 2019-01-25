package com.boostcamp.travery.main.adapter

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.data.model.Route
import com.boostcamp.travery.databinding.ItemRouteListBinding
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.main.viewholder.RouteViewHolder

class RouteListAdapter(private var listener: OnItemClickListener) : BaseAdapter<Route>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        DataBindingUtil.inflate<ItemRouteListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_route_list,
            parent,
            false
        ).also {
            it.listener = listener
            return RouteViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }
}