package com.example.yourdrive.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface Serialization {

    fun toJson(src: Any): String

    fun <T> fromJson(json: String, typeToken: TypeToken<T>): T

    class Base(private val gson: Gson = Gson()) : Serialization {
        override fun toJson(src: Any): String =
            gson.toJson(src)

        override fun <T> fromJson(json: String, typeToken: TypeToken<T>): T =
            gson.fromJson(json, typeToken.type)
    }
}