package com.boostcamp.travery.search.adapter

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.databinding.ItemActivityListBinding
import com.boostcamp.travery.search.viewholder.ActivityViewHolder

class ActivitySearchAdapter(private var listener: OnItemClickListener) : BaseAdapter<Activity>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder {
        DataBindingUtil.inflate<ItemActivityListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_activity_list,
            parent,
            false
        ).apply {
            listener = this@ActivitySearchAdapter.listener
            return ActivityViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }
}