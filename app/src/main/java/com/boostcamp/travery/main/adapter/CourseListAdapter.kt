package com.boostcamp.travery.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.boostcamp.travery.OnItemClickListener
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.databinding.ItemCourseListBinding
import com.boostcamp.travery.databinding.ItemCourseListGroupBinding
import com.boostcamp.travery.main.viewholder.CourseViewHolder
import com.boostcamp.travery.main.viewholder.GroupItem
import com.boostcamp.travery.main.viewholder.GroupViewHolder

class CourseListAdapter(private val listener: OnItemClickListener) : BaseAdapter<Any, BaseViewHolder>() {

    companion object {
        private const val VIEW_TYPE_GROUP = 1
        private const val VIEW_TYPE_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = DataBindingUtil.inflate<ItemCourseListGroupBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_course_list_group,
                        parent,
                        false)
                GroupViewHolder(binding)
            }

            else -> {
                val binding = DataBindingUtil.inflate<ItemCourseListBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_course_list,
                        parent,
                        false)
                binding.listener = this@CourseListAdapter.listener
                CourseViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is GroupItem) VIEW_TYPE_GROUP
        else VIEW_TYPE_ITEM
    }
}