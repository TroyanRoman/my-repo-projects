package com.skillbox.ascent.utils

import android.annotation.SuppressLint
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class MoshiDateAdapter {
    @SuppressLint("SimpleDateFormat")
    @FromJson
    fun fromJson(value : String): Date? {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(value)
    }

    @SuppressLint("SimpleDateFormat")
    @ToJson
    fun toJson (date: Date) : String {
        return SimpleDateFormat("yyyy.MM.dd").format(date)
    }
}