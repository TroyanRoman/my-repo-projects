package com.skillbox.ascent.utils

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(millisSinceEpoch : Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromDate(date: Date) : Long {
        return date.time
    }
}