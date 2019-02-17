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

    fun parseDateAsString(date: Long = System.currentTimeMillis(), pattern: String = DATE_PATTERN): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(date)).toString()
    }

    fun parseDateAsString(date: Date, pattern: String = DATE_PATTERN): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date).toString()
    }

    fun parseStringToDate(date: String, pattern: String = DATE_PATTERN): Date {
        return SimpleDateFormat(pattern, Locale.getDefault()).parse(date)
    }

    fun getTermDay(fromMillis: Long = System.currentTimeMillis(), toMillis: Long): Int {
        val fromDays = Calendar.getInstance(Locale.getDefault()).let {
            it.timeInMillis = fromMillis
            it.get(Calendar.DATE)
        }
        val toDays = Calendar.getInstance(Locale.getDefault()).let {
            it.timeInMillis = toMillis
            it.get(Calendar.DATE)
        }

        return Math.abs(toDays - fromDays)
    }
}