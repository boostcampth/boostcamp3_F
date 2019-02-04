package com.boostcamp.travery.coursedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionDetailLeftemptyBinding
import com.boostcamp.travery.databinding.ItemUseractionDetailLeftendBinding
import com.boostcamp.travery.databinding.ItemUseractionDetailLeftlistBinding
import com.boostcamp.travery.utils.toImage


class UserActionLeftListAdapter(userActionList: ObservableList<UserAction?>) :
        ObservableRecyclerViewAdapter<UserAction?, RecyclerView.ViewHolder>(userActionList) {

    companion object {
        const val TYPE_ACTION = 0
        const val TYPE_ENDPOINT = 1
        const val TYPE_EMPTY_ACTIVITY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACTION -> ActivityViewHolder(ItemUseractionDetailLeftlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_ENDPOINT -> EndViewHolder(ItemUseractionDetailLeftendBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> {
                EmptyViewHolder(ItemUseractionDetailLeftemptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActivityViewHolder -> {
                holder.binding.ivUnits.setImageResource(((position + 1) % 10).toImage())
                holder.binding.ivTens.setImageResource(((position + 1) % 100 / 10).toImage())
                holder.binding.root.setOnClickListener { onItemClickListener?.invoke(position) }
            }
            is EndViewHolder -> holder.binding.root.setOnClickListener { onItemClickListener?.invoke(position) }
            is EmptyViewHolder -> holder.binding.root.setOnClickListener { onItemClickListener?.invoke(position) }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            position == itemCount - 1 -> TYPE_EMPTY_ACTIVITY
            position == itemCount - 2 -> TYPE_ENDPOINT
            else -> TYPE_ACTION
        }
    }


    //활동 아이템을 위한 뷰홀더
    class ActivityViewHolder(var binding: ItemUseractionDetailLeftlistBinding) : RecyclerView.ViewHolder(binding.root)

    //끝점 아이템을 표시하기 위한 뷰홀더
    class EndViewHolder(var binding: ItemUseractionDetailLeftendBinding) : RecyclerView.ViewHolder(binding.root)

    //리사이클러뷰 아이템을 끝까지 스크롤 하기위해 마지막에 추가하는 뷰홀더
//    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class EmptyViewHolder(var binding: ItemUseractionDetailLeftemptyBinding) : RecyclerView.ViewHolder(binding.root)
}