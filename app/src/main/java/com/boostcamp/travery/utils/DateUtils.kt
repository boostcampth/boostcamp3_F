package com.boostcamp.travery.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_PATTERN = "yyyy.MM.dd"

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

    fun getTotalTime(time: Long = System.currentTimeMillis()): String {
        val min = time / 60000
        val stringTime = when {
            min < 0L -> parseDateAsString(time, "ss초")
            min < 60 -> parseDateAsString(time, "mm분 ss초")
            min < 1440 -> parseDateAsString(time, "HH시간 mm분 ss초")
            else -> parseDateAsString(time, "dd일 HH시간 mm분 ss초")
        }
        return stringTime
    }

    fun getToday(): Long {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val today = parseDateAsString(date, "yyyyMMdd")
        val dates = parseStringToDate(today, "yyyyMMdd")
        return dates.time
    }
}