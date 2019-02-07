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

    fun getDateToString(date: Long = System.currentTimeMillis()): String {
        return SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(Date(date)).toString()
    }

    fun getTermDay(fromMillis: Long = System.currentTimeMillis(), toMillis: Long): Int {
        val oneDay = 1000 * 60 * 60 * 24L
        return ((fromMillis - toMillis) / oneDay).toInt()
    }
}