package com.boostcamp.travery.base

import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder>() {
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