package com.boostcamp.travery.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.base.BaseViewHolder
import com.boostcamp.travery.base.ObservableRecyclerViewAdapter
import com.boostcamp.travery.data.remote.model.NewsFeed
import com.boostcamp.travery.databinding.ItemNewsfeedBinding
import org.json.JSONArray

class NewsFeedListAdapter(newsFeedList: ObservableList<NewsFeed>) :
        ObservableRecyclerViewAdapter<NewsFeed, RecyclerView.ViewHolder>(newsFeedList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FeedViewHolder(ItemNewsfeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FeedViewHolder).binding.vpActionImage.id = position
        holder.binding.pivActionImage.setViewPager(holder.binding.vpActionImage)
        holder.binding.item = getItem(position)
        holder.binding.executePendingBindings()

    }

    class FeedViewHolder(var binding: ItemNewsfeedBinding) : BaseViewHolder(binding) {
        override fun bind(item: Any) {

            if (item is NewsFeed) {
                binding.item = item


                if (item.image.isEmpty()) {
                    binding.vpActionImage.visibility = View.GONE

                    //계속되는 viewPager adapter 생성을 막기 위한 조건
                    (binding.vpActionImage.adapter as? ViewPagerAdapter)?.clear()
                } else {
                    binding.vpActionImage.visibility = View.VISIBLE
                    val jsonArray = JSONArray(item.image)
                    val imageList = ArrayList<String>()
                    for (i in 0 until jsonArray.length()) {
                        imageList.add(jsonArray[i] as String)
                    }

                    if (binding.vpActionImage.adapter == null) {
                        binding.vpActionImage.adapter = ViewPagerAdapter(imageList)
                    } else {
                        (binding.vpActionImage.adapter as ViewPagerAdapter).itemChange(imageList)
                    }

                }
            }

        }
    }
}