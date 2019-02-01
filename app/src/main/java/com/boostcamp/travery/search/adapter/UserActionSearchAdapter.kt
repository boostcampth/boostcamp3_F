package com.boostcamp.travery.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionListBinding
import com.boostcamp.travery.search.viewholder.UserActionViewHolder

class UserActionSearchAdapter(private var listener: OnItemClickListener) : BaseAdapter<UserAction, BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder {
        DataBindingUtil.inflate<ItemUseractionListBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_useraction_list,
                parent,
                false
        ).apply {
            listener = this@UserActionSearchAdapter.listener
            return UserActionViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }
}