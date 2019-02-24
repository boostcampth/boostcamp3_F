package com.boostcamp.travery.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableList
import com.boostcamp.travery.Constants
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.model.Bar
import com.boostcamp.travery.data.model.BaseItem
import com.boostcamp.travery.data.model.Course
import com.boostcamp.travery.data.model.Guide
import com.boostcamp.travery.data.remote.model.NewsFeed
import com.boostcamp.travery.databinding.*
import com.bumptech.glide.load.engine.DiskCacheStrategy

class NewsFeedListAdapter(itemList: ObservableList<BaseItem>) :
        ObservableRecyclerViewAdapter<BaseItem, BaseViewHolder>(itemList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            Constants.TYPE_GIUDELINE -> GuideViewHolder(
                    ItemGuideBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    ), onItemClickListener
            )
            Constants.TYPE_TOP_BAR -> BarTopViewHolder(
                    ItemTopBarBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
            Constants.TYPE_COURSE -> CourseViewHolder(
                    ItemFeedCourseBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    ), onItemClickListener
            )
            Constants.TYPE_MIDDLE_BAR -> BarMiddleViewHolder(
                    ItemMiddleBarBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    ), onItemClickListener
            )
            Constants.TYPE_BOTTOM_BAR -> BarBottomViewHolder(
                    ItemBottomBarBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
            else -> {
                FeedViewHolder(ItemNewsfeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }


    /**
     * 가이드 뷰
     */
    class GuideViewHolder(var binding: ItemGuideBinding, private val listener: ((item: Any) -> Unit)?) :
            BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.item = item as Guide
            binding.ivCancle.setOnClickListener { listener?.invoke(item) }
            binding.executePendingBindings()
        }
    }

    /**
     * 코스 목록의 상단 타이틀 뷰
     */
    class BarTopViewHolder(var binding: ItemTopBarBinding) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.item = item as Bar
            binding.executePendingBindings()
        }
    }

    /**
     * 코스 목록
     */
    class BarMiddleViewHolder(var binding: ItemMiddleBarBinding, private val listener: ((item: Any) -> Unit)?) :
            BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.item = item as Bar
            binding.root.setOnClickListener { listener?.invoke(item) }
        }
    }

    /**
     * 코스 목록 하단 뷰
     */
    class BarBottomViewHolder(var binding: ItemBottomBarBinding) : BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.item = item as Bar
        }
    }

    class CourseViewHolder(var binding: ItemFeedCourseBinding, private val listener: ((item: Any) -> Unit)?) :
            BaseViewHolder(binding) {
        override fun bind(item: Any) {
            binding.item = item as Course
            binding.root.setOnClickListener { listener?.invoke(item) }
            binding.executePendingBindings()
        }
    }

    class FeedViewHolder(var binding: ItemNewsfeedBinding) : BaseViewHolder(binding) {
        override fun bind(item: Any) {

            binding.vpActionImage.id = adapterPosition
            binding.pivActionImage.setViewPager(binding.vpActionImage)

            GlideApp.with(binding.ivFeedUserimage.context)
                    .load(binding.ivFeedUserimage.context.resources.getString(R.string.base_image_url) + (item as NewsFeed).user.image)
                    .circleCrop()
                    .error(R.mipmap.ic_launcher)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivFeedUserimage)

            binding.item = item
            binding.executePendingBindings()

        }
    }
}