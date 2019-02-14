package com.boostcamp.travery.utils

import android.animation.ObjectAnimator
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.boostcamp.travery.GlideApp
import com.boostcamp.travery.R
import com.boostcamp.travery.feed.ViewPagerAdapter
import com.boostcamp.travery.main.MainViewModel
import com.boostcamp.travery.main.adapter.CourseListAdapter
import com.boostcamp.travery.search.SearchResultViewModel
import com.boostcamp.travery.search.UserActionSearchAdapter
import com.boostcamp.travery.useraction.detail.UserActionDetailViewModel
import com.boostcamp.travery.useraction.detail.UserActionImageAdapter
import com.boostcamp.travery.useraction.save.UserActionImageListAdapter
import com.boostcamp.travery.useraction.save.UserActionSaveViewModel
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


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
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                    ): Boolean {
                        imageView.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(imageView)
    }

    /**
     * 둥근 이미지형태로 로드.
     */
    @JvmStatic
    @BindingAdapter("circleImage")
    fun setCircleImage(imageView: ImageView, path: String) {
        GlideApp.with(imageView.context)
                .load(path)
                .circleCrop()
                .error(R.mipmap.ic_launcher)
                .into(imageView)
    }

    /**
     * 모서리가 둥근 이미지형태로 로드.
     */
    @JvmStatic
    @BindingAdapter("roundedImage")
    fun setRoundedImage(imageView: ImageView, path: String?) {
        GlideApp.with(imageView.context)
                .load(path)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(10)))
                .error(R.drawable.empty_image)
                .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("android:date")
    fun setDate(textView: TextView, date: Date?) {
        date?.let{textView.text = DateUtils.parseDateAsString(date, "yyyy.MM.dd")}
    }

    @JvmStatic
    @BindingAdapter("bind:startTime", "bind:endTime")
    fun setTime(textView: TextView, startTime: Long, endTime: Long) {
        textView.text = String.format(
                textView.context.resources.getString(
                        R.string.string_place_holder_date,
                        DateUtils.parseDateAsString(startTime, "yyyy.MM.dd - HH:mm"),
                        DateUtils.parseDateAsString(endTime, "HH:mm")
                )
        )
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
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("bind:fadeVisibility")
    fun setFadeVisibility(view: View, value: Boolean) {
        if (value) {
            view.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 700
                start()
            }
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bind:visibility")
    fun setVisibility(view: View, value: Boolean) {
        if (value) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bind:animatedVisibility")
    fun setAnimateVisibility(view: View, value: Boolean) {
        if (value) {
            (view.background as AnimationDrawable).start()
        } else {
            (view.background as AnimationDrawable).stop()
        }
    }

    @JvmStatic
    @BindingAdapter("bind:talk")
    fun setRandomText(view: View, value: Boolean) {
        val array = view.resources.getStringArray(R.array.talk_array)
        val randomStr = array[Random().nextInt(array.size)]
        (view as TextView).text = randomStr
    }

    @JvmStatic
    @BindingAdapter("imageAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: UserActionSaveViewModel) {
        val adapter = UserActionImageListAdapter(viewModel.imageList).apply {
            onRemoveItemClickListener = viewModel::onRemoveItemClick
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("imageAdapter")
    fun setAdapter(recyclerView: RecyclerView, viewModel: UserActionDetailViewModel) {
        val adapter = UserActionImageAdapter(viewModel.imageList).apply {
            onItemClickListener = { item: Any -> viewModel.onItemClick(item) }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    /**
     * 리사이클러뷰 해당 position이 리사이클러뷰의 최상단에 보이게 스크롤
     */
    @JvmStatic
    @BindingAdapter("scrollTo")
    fun scrollTo(recyclerView: RecyclerView, position: Int) {
        recyclerView.smoothScrollToPosition(position)
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

    /**
     * listOf("#해시태그1","해시태그2","해시태그3") 형태로 들어오면, 각각을 Chip 으로 세팅하여 addView
     * 이 메서드가 다시 호출되면 또 추가될 가능성이 있음.
     */
    @JvmStatic
    @BindingAdapter("hashTag")
    fun setHashTag(chipGroup: ChipGroup, hashTagList: List<String>) {
        hashTagList.forEach { hashTag ->
            Chip(chipGroup.context).apply {
                text = hashTag
                isClickable = true
                isChipIconVisible = false
                isCheckedIconVisible = false
            }.also {
                chipGroup.addView(it)
            }
        }
    }

    /**
     * 선택한 방향으로 isAnimated 값에 따라 translation 애니메이션이 시작됨.
     * 애니메이션이 끝나고 끝난자리에 고정됨.
     *
     * @param direction 애니메이션이 될 방향을 지정
     * @param isAnimated true시 화면 밖으로 사라짐 down의 경우 true 시 나타남.
     */
    @JvmStatic
    @BindingAdapter("bind:direction", "bind:isAnimated")
    fun setAnimation(view: View, direction: String, isAnimated: Boolean) {
        when (direction) {
            "up" -> {
                if (isAnimated) {
                    view.animate().translationY(-view.height.toFloat()).alpha(0.0f).withLayer()
                } else {
                    view.animate().translationY(0.0f).alpha(1.0f).withLayer()
                }
            }
            "left" -> {
                if (isAnimated) {
                    view.animate().translationX(-view.width.toFloat()).withLayer()
                } else {
                    view.animate().translationX(0.0f).withLayer()
                }
            }
            "down" -> {
                if (isAnimated) {
                    view.animate().translationY(view.height.toFloat() + 8.toPx()).withLayer()
                } else {
                    view.animate().translationY(0.0f).withLayer()

                }
            }
        }

    }

    /**
     * FeedViewPager에 adapter 세팅
     */

    @JvmStatic
    @BindingAdapter("setViewPagerAdapter")
    fun setViewPagerAdapter(viewpager: ViewPager, jsonString: String) {
        if (jsonString.isEmpty()) {
            viewpager.visibility = View.GONE
            //계속되는 viewPager adapter 생성을 막기 위한 조건
            if (viewpager.adapter == null) {
            } else {
                (viewpager.adapter as ViewPagerAdapter).clear()
            }
        } else {
            viewpager.visibility = View.VISIBLE
            val jsonArray = JSONArray(jsonString)
            val imageList = ArrayList<String>()
            for (i in 0 until jsonArray.length()) {
                imageList.add(jsonArray[i] as String)
            }
            if (viewpager.adapter == null) {
                viewpager.adapter = ViewPagerAdapter(imageList)
            } else {
                (viewpager.adapter as ViewPagerAdapter).itemChange(imageList)
            }
        }

    }

    @JvmStatic
    @BindingAdapter("feedDate")
    fun setDateCompareToNowDate(textView: AppCompatTextView, stringDate: String) {

        val writeDate = DateUtils.parseStringToDate(stringDate, "yyyy-MM-dd HH:mm:ss")
        val min = (Date().time - writeDate.time) / 60000
        var stringTime = ""
        when {
            min == 0L -> stringTime = "방금"
            min < 60 -> stringTime = min.toString() + "분 전"
            min < 1440 -> {
                val hour = min / 60
                stringTime = hour.toString() + "시간 전"
            }
            min < 525600 -> stringTime = DateUtils.parseDateAsString(writeDate, "MM월 dd일 HH:mm")
            else -> stringTime = DateUtils.parseDateAsString(writeDate, "yyyy년 MM월 dd일 HH:mm")
        }
        textView.text = stringTime
    }

    @JvmStatic
    @BindingAdapter("onSeekChanged")
    fun onSeekChangedListener(view: IndicatorSeekBar, listener: OnSeekChangeListener) {
        view.onSeekChangeListener = listener
    }
}