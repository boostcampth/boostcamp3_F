package com.boostcamp.travery.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionDetailLeftendBinding
import com.boostcamp.travery.databinding.ItemUseractionDetailLeftlistBinding
import com.boostcamp.travery.utils.toImage


class UserActionLeftListAdapter(var dataList: List<UserAction?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ACTIVITY = 0
        const val TYPE_ENDPOINT = 1
        const val TYPE_EMPTY_ACTIVITY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACTIVITY -> ActivityViewHolder(
                ItemUseractionDetailLeftlistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_ENDPOINT -> EndViewHolder(
                ItemUseractionDetailLeftendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> {
                val view = LinearLayout(parent.context)
                view.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
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

    //활동 아이템을 위한 뷰홀더
    class ActivityViewHolder(var binding: ItemUseractionDetailLeftlistBinding) : RecyclerView.ViewHolder(binding.root)

    //끝점 아이템을 표시하기 위한 뷰홀더
    class EndViewHolder(var binding: ItemUseractionDetailLeftendBinding) : RecyclerView.ViewHolder(binding.root)

    //리사이클러뷰 아이템을 끝까지 스크롤 하기위해 마지막에 추가하는 뷰홀더
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}