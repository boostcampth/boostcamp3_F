package com.boostcamp.travery.coursedetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.R
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionDetailBinding
import com.boostcamp.travery.databinding.ItemUseractionEndpointBinding
import com.boostcamp.travery.utils.toImage

class UserActionListAdapter(userActionList: ObservableList<UserAction>) :
        ObservableRecyclerViewAdapter<UserAction, RecyclerView.ViewHolder>(userActionList) {
    companion object {
        const val TYPE_ACTION = 0
        const val TYPE_EMPTY_ACTIVITY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ACTION) {
            ActivityViewHolder(ItemUseractionDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ActivityEmptyViewHolder(ItemUseractionEndpointBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("TopListAdapter", "" + position)
        if (holder is ActivityEmptyViewHolder) {
            //아이템이 0번째라면 시작 이미지
            if (position == 0) {
                holder.binding.ivEndpoint.setImageResource(R.drawable.ic_map_start)
            } else {
                holder.binding.ivEndpoint.setImageResource(R.drawable.ic_map_arrive)
            }
            holder.binding.item = getItem(position)
            holder.binding.executePendingBindings()
        } else if (holder is ActivityViewHolder) {
            holder.binding.root.setOnClickListener { onItemClickListener?.invoke(getItem(position)) }
            val tens = position % 100 / 10
            val units = position % 10
            holder.binding.ivTens.setImageResource(tens.toImage())
            holder.binding.ivUnits.setImageResource(units.toImage())
            holder.binding.item = getItem(position)
            Log.e("USER", getItem(position).toString())
            holder.binding.executePendingBindings()
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_EMPTY_ACTIVITY
            itemCount - 1 -> TYPE_EMPTY_ACTIVITY
            else -> TYPE_ACTION
        }
    }

    //기록된 활동들에 대한 뷰홀더
    class ActivityViewHolder(var binding: ItemUseractionDetailBinding) : RecyclerView.ViewHolder(binding.root)

    //시작점과 끝점 아이템을 위한 뷰홀더
    class ActivityEmptyViewHolder(var binding: ItemUseractionEndpointBinding) : RecyclerView.ViewHolder(binding.root)

}