package com.boostcamp.travery.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.databinding.ItemUseractionListBinding

class SearchResultListAdapter() :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userActionList = ArrayList<UserAction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchResultListAdapter.UserActionViewHolder(ItemUseractionListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserActionViewHolder).binding.item = userActionList[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return userActionList.size
    }

    //기록된 활동들에 대한 뷰홀더
    class UserActionViewHolder(var binding: ItemUseractionListBinding) : RecyclerView.ViewHolder(binding.root)


    fun updateListItems(userAction: ArrayList<UserAction>) {
        val diffCallback = UserActionDiffUtilCallBack(userAction, userActionList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        userActionList.clear()
        userActionList.addAll(userAction)
        diffResult.dispatchUpdatesTo(this)
    }
}