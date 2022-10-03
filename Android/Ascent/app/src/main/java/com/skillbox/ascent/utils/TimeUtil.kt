package com.skillbox.ascent.utils


import com.skillbox.ascent.utils.Constants.TIME_UNITS
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

object TimeUtil {

    fun getFormattedTime(timeInMillis: Long): String {
        var milliseconds = timeInMillis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"


    }

    fun calculateElapsingTime(hoursDiff: Int, minutesDiff: Int): Int {
        val elapsedTimeInSec = hoursDiff * TIME_UNITS * TIME_UNITS + minutesDiff * TIME_UNITS
        if (elapsedTimeInSec <= 0) throw IllegalArgumentException()
        return elapsedTimeInSec
    }

    fun setCreateTimeString(hoursDiff: Int, minutesDiff: Int): String {
        return when {
            hoursDiff > 1 && minutesDiff > 0 -> "$hoursDiff h $minutesDiff min "
            hoursDiff > 1 && minutesDiff == 0 -> "$hoursDiff h"
            hoursDiff > 1 && minutesDiff < 0 -> "${hoursDiff - 1} h ${TIME_UNITS + minutesDiff} min "
            hoursDiff == 1 && minutesDiff > 0 -> "1 h $minutesDiff min "
            hoursDiff == 1 && minutesDiff == 0 -> "1 h "
            hoursDiff == 1 && minutesDiff < 0 -> "${60 + minutesDiff} min "
            hoursDiff == 0 && minutesDiff > 0 -> "$minutesDiff min "
            else ->  throw IllegalArgumentException()

        }
    }


}