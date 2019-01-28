package com.boostcamp.travery.routedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.databinding.ItemActivityDetailLeftendBinding
import com.boostcamp.travery.databinding.ItemActivityDetailLeftlistBinding
import com.boostcamp.travery.utils.toImage


class ActivityLeftListAdapter(var dataList: List<Activity?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ACTIVITY = 0
        const val TYPE_ENDPOINT = 1
        const val TYPE_EMPTY_ACTIVITY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACTIVITY -> ActivityViewHolder(
                ItemActivityDetailLeftlistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_ENDPOINT -> EndViewHolder(
                ItemActivityDetailLeftendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else ->{
                val view = LinearLayout(parent.context)
                view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActivityViewHolder) {
            holder.binding.ivUnits.setImageResource(((position + 1) % 10).toImage())
            holder.binding.ivTens.setImageResource(((position + 1) % 100 / 10).toImage())
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList[position] != null -> TYPE_ACTIVITY
            position != dataList.size - 1 -> TYPE_ENDPOINT
            else -> TYPE_EMPTY_ACTIVITY
        }
    }

    class ActivityViewHolder(var binding: ItemActivityDetailLeftlistBinding) : RecyclerView.ViewHolder(binding.root)

    class EndViewHolder(var binding: ItemActivityDetailLeftendBinding) : RecyclerView.ViewHolder(binding.root)

    class EmptyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){}
}