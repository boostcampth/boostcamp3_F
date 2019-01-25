package com.boostcamp.travery.main.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Route
import com.boostcamp.travery.databinding.ItemRouteListBinding
import com.boostcamp.travery.databinding.ItemRouteListGroupBinding
import com.boostcamp.travery.main.viewholder.BaseViewHolder
import com.boostcamp.travery.main.viewholder.RouteGroupViewHolder
import com.boostcamp.travery.main.viewholder.RouteViewHolder
import java.util.*

class RouteListAdapter(private var listener: OnItemClickListener) : RecyclerView.Adapter<BaseViewHolder>() {
    private val data = ArrayList<Route>()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_GROUP = 2
    }

    override fun getItemViewType(position: Int): Int {
        // 뷰타입 정의 필요
        return VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = DataBindingUtil.inflate<ItemRouteListGroupBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_route_list,
                    parent,
                    false
                )
                RouteGroupViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemRouteListBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_route_list,
                    parent,
                    false
                )
                binding.listener = listener
                RouteViewHolder(binding)
            }
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    private fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }

    fun setItems(data: List<Route>) {
        clearItems()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItems(data: List<Route>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItem(data: Route, position: Int) {
        this.data.add(data)
        notifyItemInserted(position)
    }
}