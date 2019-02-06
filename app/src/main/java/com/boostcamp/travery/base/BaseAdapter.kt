package com.boostcamp.travery.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
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
        val position = this.data.size
        this.data.addAll(data)
        notifyItemRangeInserted(position, data.size)
    }

    fun addItem(position: Int, data: T) {
        this.data.add(position, data)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}