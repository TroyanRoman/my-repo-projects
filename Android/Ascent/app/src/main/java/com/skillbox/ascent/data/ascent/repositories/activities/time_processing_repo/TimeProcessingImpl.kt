package com.skillbox.ascent.data.ascent.repositories.activities.time_processing_repo

import android.util.Log
import java.lang.IllegalArgumentException
import javax.inject.Inject

class TimeProcessingImpl @Inject constructor(): TimeProcessing {

    override fun calculateElapsingTime(hoursDiff: Int, minutesDiff: Int): Int {
        val elapsedTimeInSec = hoursDiff * 3600 + minutesDiff * 60
        Log.d("TimeProcessing", "hoursDiff = $hoursDiff minDiff = $minutesDiff   calculateMethod")
        if (elapsedTimeInSec <= 0) throw IllegalArgumentException()
        return elapsedTimeInSec
    }

    override fun setCreateTimeString(hoursDiff: Int, minutesDiff: Int): String {
        Log.d("CreateActivityLog", "hoursDiff = $hoursDiff minDiff = $minutesDiff   setElapsedTimeMethod")
        return when {
            hoursDiff > 1 && minutesDiff > 0 -> "$hoursDiff h $minutesDiff min "
            hoursDiff > 1 && minutesDiff <= 0 -> "${hoursDiff - 1} h ${TIME_UNITS + minutesDiff} min "
            hoursDiff == 1 && minutesDiff < 0 -> "${60 + minutesDiff} min "
            hoursDiff == 0 && minutesDiff > 0 -> "$minutesDiff min "
            else ->  throw IllegalArgumentException()

        }
    }


    companion object {
        const val TIME_UNITS = 60
    }
}