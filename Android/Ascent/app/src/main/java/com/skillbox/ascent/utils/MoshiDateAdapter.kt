package com.skillbox.ascent.utils

import android.annotation.SuppressLint
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class MoshiDateAdapter {

    @FromJson
    fun fromJson(value : String): Date? {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(value)
    }


    @ToJson
    fun toJson (date: Date) : String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
    }
}