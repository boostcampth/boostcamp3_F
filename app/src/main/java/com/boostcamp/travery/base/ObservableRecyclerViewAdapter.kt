package com.boostcamp.travery.base

import android.util.Log
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView

abstract class ObservableRecyclerViewAdapter<T, Holder : RecyclerView.ViewHolder>(
    private val items: ObservableList<T>
) : RecyclerView.Adapter<Holder>() {

    var onItemClickListener: ((item: Any) -> Unit)? = null

    init {
        items.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
            override fun onChanged(sender: ObservableList<T>?) {
                Log.e("ObservableAdapter","Asd")
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                Log.e("ObservableAdapter","onItemRangeRemoved")
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<T>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyDataSetChanged()
            }

            override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                Log.e("ObservableAdapter","onItemRangeInserted"+positionStart+"/"+itemCount)
                notifyItemRangeInserted(positionStart,itemCount)
            }

            override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                Log.e("ObservableAdapter","onItemRangeChanged")
                notifyItemRangeChanged(positionStart+1, itemCount)
            }
        })
    }

    override fun getItemCount(): Int = items.size

    fun getItem(i: Int): T {
        return items[i]
    }

    fun getItems(): ObservableList<T> {
        return items
    }


}