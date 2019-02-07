package com.boostcamp.travery.useractiondetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseAdapter
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.databinding.ItemUseractionDetailImageBinding

class UserActionImageAdapter : BaseAdapter<String, BaseViewHolder>() {
    var onItemClickListener: ((item: Any) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemUseractionDetailImageBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_useraction_detail_image,
                parent,
                false)
        return ImageViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }


    inner class ImageViewHolder(private val binding: ItemUseractionDetailImageBinding,
                                private val onItemClickListener: ((item: Any) -> Unit)?) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            if (item is String) {
                binding.item = item
                binding.root.setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }
}