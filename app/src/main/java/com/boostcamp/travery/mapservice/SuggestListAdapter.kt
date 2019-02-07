package com.boostcamp.travery.mapservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.boostcamp.travery.R
import com.google.android.gms.maps.model.LatLng

class SuggestListAdapter(
    context: Context,
    private val suggestList: ArrayList<LatLng>
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

        val context = parent.context
        viewHolder.date.text = suggestList[position].latitude.toString()
        viewHolder.title.text = "${position + 1}번째 제안"

        return view!!
    }

    data class ViewHolder(val date: TextView, val title: TextView)
}