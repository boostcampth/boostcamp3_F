package com.boostcamp.travery.utils

import android.content.Context
import com.boostcamp.travery.R


fun Number.toImage(): Int {
    return when (this) {
        0 -> R.drawable.zero
        1 -> R.drawable.one
        2 -> R.drawable.two
        3 -> R.drawable.three
        4 -> R.drawable.four
        5 -> R.drawable.five
        6 -> R.drawable.six
        7 -> R.drawable.seven
        8 -> R.drawable.eight
        9 -> R.drawable.nine
        else -> -1
    }
}

fun Number.dpToPixel(context: Context): Float {
    return this.toFloat() * (context.resources.displayMetrics.densityDpi / 160f)
}
