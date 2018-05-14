package com.igorronner.irinterstitial.utils

import java.util.*

class DateUtils {


    companion object {
        @JvmStatic
        fun daysBetween(lastDate: Date?, firstDate: Date?): Long {
            val diff = firstDate?.time?.let { lastDate?.time?.minus(it) }
            val diffDays = diff?.div(24 * 60 * 60 * 1000)
            return diffDays!!
        }
        @JvmStatic
        fun daysBetween(lastCalendar: Calendar?, endCalendar: Calendar?): Long {
            return daysBetween(lastCalendar?.time, endCalendar?.time)
        }
    }

}