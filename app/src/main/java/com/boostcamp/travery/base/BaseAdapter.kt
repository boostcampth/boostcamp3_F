package com.boostcamp.travery.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {
    protected val data = ArrayList<T>()

    override fun getItemCount() = data.size

    protected fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }

    fun setItems(data: List<T>) {
        clearItems()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItems(data: List<T>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItem(data: T, position: Int) {
        this.data.add(data)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}