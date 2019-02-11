package com.boostcamp.travery.mapservice

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.boostcamp.travery.R
import com.boostcamp.travery.data.model.Suggestion
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class SuggestListAdapter(
        context: Context,
        private val suggestList: ArrayList<Suggestion>
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return suggestList.size
    }

    override fun getItem(position: Int): Any {
        return suggestList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var view: View? = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_suggest_list, parent, false)

            viewHolder = ViewHolder(
                    view.findViewById(R.id.tv_date),
                    view.findViewById(R.id.tv_title)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val start = SimpleDateFormat("yyyy.MM.dd - kk:mm", Locale.getDefault())
        val end = SimpleDateFormat("kk:mm", Locale.getDefault())
        viewHolder.date.text =
                "${start.format(Date(suggestList[position].startTime))} ~ ${end.format(Date(suggestList[position].endTime))}"
        viewHolder.title.text = "${position + 1}번째 제안"

        return view!!
    }

    data class ViewHolder(val date: TextView, val title: TextView)
}