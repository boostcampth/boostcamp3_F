package com.boostcamp.travery.main.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ItemCourseListBinding

class CourseViewHolder(private val binding: ItemCourseListBinding) :
    BaseViewHolder(binding) {

    override fun bind(item: Any) {
        if (item is Course) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}