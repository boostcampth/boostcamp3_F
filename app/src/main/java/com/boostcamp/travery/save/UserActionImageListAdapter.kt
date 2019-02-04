package com.boostcamp.travery.save

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.databinding.ItemUseractionSaveBinding
import com.boostcamp.travery.databinding.ItemUseractionSaveImageBinding

class UserActionImageListAdapter(items: ObservableList<UserActionImage>)
    : ObservableRecyclerViewAdapter<UserActionImage, BaseViewHolder>(items) {
    init {
        items.add(UserActionImage(""))
    }

    companion object {
        const val VIEW_TYPE_ADD = 0
        const val VIEW_TYPE_ITEM = 1
    }

    lateinit var onAddItemClickListener: () -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD -> {
                val binding = DataBindingUtil.inflate<ItemUseractionSaveBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_useraction_save,
                        parent,
                        false)
                ImageAddViewHolder(binding, onAddItemClickListener)
            }

            else -> {
                val binding = DataBindingUtil.inflate<ItemUseractionSaveImageBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_useraction_save_image,
                        parent,
                        false)
                ImageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItems()[position])
    }

    override fun getItemViewType(position: Int): Int {
        if (position == getItems().size - 1) return VIEW_TYPE_ADD
        return VIEW_TYPE_ITEM
    }


    inner class ImageViewHolder(private val binding: ItemUseractionSaveImageBinding) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            if (item is UserActionImage) {
                binding.item = item
            }
        }
    }

    inner class ImageAddViewHolder(private val binding: ItemUseractionSaveBinding,
                                   private val onAddItemClickListener: () -> Unit) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.root.setOnClickListener { onAddItemClickListener.invoke() }
        }
    }
}

data class UserActionImage(val filePath: String)

