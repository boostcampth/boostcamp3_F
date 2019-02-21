package com.boostcamp.travery.useraction.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.viewpager.widget.PagerAdapter
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import kotlinx.android.synthetic.main.item_viewpager_image.view.*

class ImagesViewPagerAdapter(private val images: ObservableArrayList<String>) : PagerAdapter() {

    init {
        images.addOnListChangedCallback(object: ObservableList.OnListChangedCallback<ObservableList<String>?>() {
            override fun onChanged(sender: ObservableList<String>?) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(sender: ObservableList<String>?, positionStart: Int, itemCount: Int) {
                notifyDataSetChanged()
            }

            override fun onItemRangeMoved(sender: ObservableList<String>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                notifyDataSetChanged()
            }

            override fun onItemRangeInserted(sender: ObservableList<String>?, positionStart: Int, itemCount: Int) {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(sender: ObservableList<String>?, positionStart: Int, itemCount: Int) {
                notifyDataSetChanged()
            }
        })
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_viewpager_image, container, false)
        GlideApp.with(container.context)
                .load(images[position])
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(view.iv_image)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.invalidate()
    }

    fun clear() {
        this.images.clear()
        notifyDataSetChanged()
    }
}