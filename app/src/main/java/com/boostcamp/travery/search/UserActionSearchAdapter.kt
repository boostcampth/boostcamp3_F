package com.boostcamp.travery.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionListBinding

class UserActionSearchAdapter(private val data: ObservableArrayList<UserAction>) :
    ObservableRecyclerViewAdapter<UserAction, BaseViewHolder>(data) {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder {
        DataBindingUtil.inflate<ItemUseractionListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_useraction_list,
            parent,
            false
        ).apply {
            return UserActionViewHolder(this, onItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }
}

class UserActionViewHolder(
    private val binding: ItemUseractionListBinding, private val onItemClick: ((item: Any) -> Unit)?
) : BaseViewHolder(binding) {
    override fun bind(item: Any) {
        if (item is UserAction) {
            binding.apply {
                this.item = item
                root.setOnClickListener { onItemClick?.invoke(item) }
                executePendingBindings()
            }
        }
    }
}