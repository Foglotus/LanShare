package com.foglotus.core.util

import java.text.SimpleDateFormat
import java.util.*

/**
 *时间和日期工具类
 * @author foglotus
 * @since 2019/2/25
 */
object DateUtil {
    fun getDate(dateMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(dateMillis))
    }

    fun getDateAndTime(dateMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(dateMillis))
    }
}