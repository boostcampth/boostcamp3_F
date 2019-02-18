package com.boostcamp.travery.course.list.adapter.viewholder

import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.databinding.ItemCourseListBinding

class CourseViewHolder(private val binding: ItemCourseListBinding,
                       private val listener: ((item: Any) -> Unit)?) :
        BaseViewHolder(binding) {

    override fun bind(item: Any) {
        if (item is Course) {
            binding.apply {
                this.item = item
                root.setOnClickListener {
                    listener?.invoke(item)
                }
                executePendingBindings()
            }
        }
    }
}