package com.boostcamp.travery.presentation.useraction.save

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.databinding.ItemUseractionSaveImageBinding
import kotlinx.android.synthetic.main.item_useraction_save_image.view.*

class UserActionImageListAdapter(items: ObservableList<UserActionImage>)
    : ObservableRecyclerViewAdapter<UserActionImage, BaseViewHolder>(items) {

    lateinit var onRemoveItemClickListener: (item: UserActionImage) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                val binding = DataBindingUtil.inflate<ItemUseractionSaveImageBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_useraction_save_image,
                        parent,
                        false)
                return ImageViewHolder(binding, onRemoveItemClickListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItems()[position])
    }

    inner class ImageViewHolder(private val binding: ItemUseractionSaveImageBinding,
                                private val onRemoveItemClickListener: (item: UserActionImage) -> Unit) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            if (item is UserActionImage) {
                binding.item = item
                binding.root.btn_remove_image?.setOnClickListener {
                    onRemoveItemClickListener.invoke(item)
                }
            }
        }
    }
}

data class UserActionImage(val filePath: String)

