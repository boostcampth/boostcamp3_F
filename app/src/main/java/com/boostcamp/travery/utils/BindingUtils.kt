package com.boostcamp.travery.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import com.boostcamp.travery.main.MainViewModel
import com.boostcamp.travery.main.adapter.CourseListAdapter
import com.boostcamp.travery.save.UserActionImageListAdapter
import com.boostcamp.travery.save.UserActionSaveViewModel
import com.boostcamp.travery.search.SearchResultViewModel
import com.boostcamp.travery.search.UserActionSearchAdapter
import com.boostcamp.travery.useractiondetail.UserActionDetailViewModel
import com.boostcamp.travery.useractiondetail.UserActionImageAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        GlideApp.with(imageView.context)
                .load(path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        imageView.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
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
        val start = SimpleDateFormat("yyyy.MM.dd - HH:mm")
        val end = SimpleDateFormat("HH:mm")
        textView.text = String.format(textView.context.resources.getString(R.string.string_place_holder_date,
                start.format(Date(startTime)),
                end.format(Date(endTime))))
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
//        if (position == 0) {
//            recyclerView.scrollToPosition(0)
//        } else {
//            val top = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//            val curTop = recyclerView.findViewHolderForAdapterPosition(top)?.itemView?.top ?: 0
//            val positionTop = recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.top
//                    ?: 0
//            recyclerView.scrollBy(0, positionTop - curTop)
//        }

        //scrollToPositionWithOffset 시 이동한 해당 아이템 값 노출이 안됨.
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)

    }

    @JvmStatic
    @BindingAdapter("listAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: MainViewModel) {
        val adapter = CourseListAdapter(viewModel.data).apply {
            onItemClickListener = { item: Any -> viewModel.onItemClick(item) }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context)
            this.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("listAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: SearchResultViewModel) {
        val adapter = UserActionSearchAdapter(viewModel.data).apply {
            onItemClickListener = { item: Any -> viewModel.onItemClick(item) }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context)
            this.adapter = adapter
        }
    }

    /**
     * 선택한 테마에 맞추어 배경 변경(총 5가지 색상)
     */
    @JvmStatic
    @BindingAdapter("theme")
    fun setTheme(textView: TextView, theme: String) {
        val string = textView.resources.getStringArray(R.array.theme_array)
        textView.apply {
            text = theme
            background = when (theme) {
                string[0] -> ContextCompat.getDrawable(textView.context, R.drawable.bg_theme_red)
                string[1] -> ContextCompat.getDrawable(textView.context, R.drawable.bg_theme_pink)
                string[2] -> ContextCompat.getDrawable(textView.context, R.drawable.bg_theme_deep_purple)
                string[3] -> ContextCompat.getDrawable(textView.context, R.drawable.bg_theme_indigo)
                else -> ContextCompat.getDrawable(textView.context, R.drawable.bg_theme_green)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("group")
    fun setGroupTitle(textView: TextView, group: Long) {
        val termDay = DateUtils.getTermDay(toMillis = group)
        textView.text = when (termDay) {
            0 -> textView.resources.getString(R.string.string_group_title_today)
            1 -> textView.resources.getString(R.string.string_group_title_yesterday)
            else -> DateUtils.getDateToString(group)
        }
    }
}