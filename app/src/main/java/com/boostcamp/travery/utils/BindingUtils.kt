package com.boostcamp.travery.utils

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.boostcamp.travery.save.UserActionImageListAdapter
import com.boostcamp.travery.save.UserActionSaveViewModel
import com.boostcamp.travery.useractiondetail.UserActionDetailViewModel
import com.boostcamp.travery.useractiondetail.UserActionImageAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nex3z.flowlayout.FlowLayout
import java.text.SimpleDateFormat
import java.util.*


object BindingUtils {
    @JvmStatic
    @BindingAdapter("android:mapImage")
    fun setMapImage(imageView: ImageView, path: String) {
        if (!path.isEmpty()) {
            GlideApp.with(imageView.context)
                    .load(path)
                    .circleCrop()
                    .into(imageView)
        } else {
            imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                            imageView.context,
                            com.boostcamp.travery.R.mipmap.ic_launcher_round
                    )
            )
        }
    }

    @JvmStatic
    @BindingAdapter("image")
    fun setImage(imageView: ImageView, path: String?) {
        Glide.with(imageView.context)
                .load(path)
                .apply(RequestOptions().centerCrop().error(R.mipmap.ic_launcher))
                .into(imageView)
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("android:date")
    fun setDate(textView: TextView, date: Date) {
        val dayTime = SimpleDateFormat("yyyy.MM.dd")
        textView.text = dayTime.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("bind:startTime", "bind:endTime")
    fun setTime(textView: TextView, startTime: Long, endTime: Long) {
        val start = SimpleDateFormat("yyyy.MM.dd - kk:mm")
        val end = SimpleDateFormat("kk:mm")
        textView.text = "${start.format(Date(startTime))} ~ ${end.format(Date(endTime))}"
    }

    @JvmStatic
    @BindingAdapter("setAdapter")
    fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
        val dividerItemDecoration =
                DividerItemDecoration(recyclerView.context, LinearLayoutManager(recyclerView.context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }


    @JvmStatic
    @BindingAdapter("setAdapter")
    fun bindMultiSnapRecyclerViewAdapter(
            recyclerView: com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView,
            adapter: RecyclerView.Adapter<*>
    ) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("bind:visibility")
    fun setVisibility(view: View, value: Boolean) {
        if (value) {
            view.requestFocus()
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    @JvmStatic
    @BindingAdapter("imageAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: UserActionSaveViewModel) {
        val adapter = UserActionImageListAdapter(viewModel.imageList).apply {
            onAddItemClickListener = viewModel::onAddItemClick
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("imageAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: UserActionDetailViewModel) {
        val adapter = UserActionImageAdapter().apply {
            setItems(viewModel.imageList)
            onItemClickListener = { item: Any -> viewModel.onItemClick(item) }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("hashTag")
    fun setHashTag(flowLayout: FlowLayout, hashTagList: List<String>) {
        hashTagList.forEach {
            val tv = TextView(ContextThemeWrapper(flowLayout.context, R.style.BorderTextView)).apply {
                text = it
            }
            (tv.parent as? ViewGroup)?.removeView(tv)
            flowLayout.addView(tv)
        }
    }

    /**
     * 리사이클러뷰 해당 position이 리사이클러뷰의 최상단에 보이게 스크롤
     */
    @JvmStatic
    @BindingAdapter("scrollTo")
    fun scrollTo(recyclerView: RecyclerView, position: Int) {
        //리사이클러뷰의 최상단으로 scrollBy를 이용하여 하면 작동이 잘안되는 문제로
        //position==0일때만 scrollToPosition을 사용
        if (position == 0) {
            recyclerView.scrollToPosition(0)
        } else {
            val top = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            val curTop = recyclerView.findViewHolderForAdapterPosition(top)?.itemView?.top ?: 0
            val positionTop = recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.top
                    ?: 0
            recyclerView.scrollBy(0, positionTop - curTop)
        }
    }

}