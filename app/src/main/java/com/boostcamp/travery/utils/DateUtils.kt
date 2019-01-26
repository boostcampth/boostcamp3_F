package com.boostcamp.travery.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_PATTERN = "yyyy.MM.dd"

    fun getDate(date: Long = System.currentTimeMillis()): List<Int> {
        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return dateFormat.format(Date(date)).toString().split(".").map {
            it.toInt()
        }
    }

    fun getTermDay(fromMillis: Long = System.currentTimeMillis(), toMillis: Long): Int {
        val oneDay = 1000 * 60 * 24L
        return ((fromMillis - toMillis) / oneDay).toInt()
    }
}