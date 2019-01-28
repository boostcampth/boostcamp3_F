package com.boostcamp.travery.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(binding: ViewDataBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: Any)
}