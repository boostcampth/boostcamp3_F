package com.boostcamp.travery.routedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.databinding.ItemActivityDetailListBinding
import com.boostcamp.travery.databinding.ItemActivityEmptyBinding
import com.boostcamp.travery.utils.toImage

class ActivityTopListAdapter(var dataList: List<Activity?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ACTIVITY = 0
        const val TYPE_EMPTY_ACTIVITY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ACTIVITY) {
            return ActivityViewHolder(
                ItemActivityDetailListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ActivityEmptyViewHolder(
                ItemActivityEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActivityEmptyViewHolder) {
            //아이템이 0번째라면 시작 이미지
            if (position == 0) {
                holder.binding.ivEndpoint.setImageResource(R.drawable.start)
            } else {
                holder.binding.ivEndpoint.setImageResource(R.drawable.finish)
            }
            holder.binding.activity = dataList[position]
        } else if (holder is ActivityViewHolder) {
            var tens = position % 100 / 10
            var units = position % 10
            holder.binding.ivTens.setImageResource(tens.toImage())
            holder.binding.ivUnits.setImageResource(units.toImage())
            holder.binding.activity = dataList[position]
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] != null) TYPE_ACTIVITY else TYPE_EMPTY_ACTIVITY
    }


    //기록된 활동들에 대한 뷰홀더
    class ActivityViewHolder(var binding: ItemActivityDetailListBinding) : RecyclerView.ViewHolder(binding.root)

    //시작점과 끝점 아이템을 위한 뷰홀더
    class ActivityEmptyViewHolder(var binding: ItemActivityEmptyBinding) : RecyclerView.ViewHolder(binding.root)


}