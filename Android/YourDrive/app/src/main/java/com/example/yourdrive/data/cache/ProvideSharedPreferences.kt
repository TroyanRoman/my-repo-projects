package com.example.yourdrive.data.cache

import android.content.Context
import android.content.SharedPreferences

interface ProvideSharedPreferences {

    fun sharedPreferences(): SharedPreferences

    class Base(
        private val context: Context,
        private val visibility: Int = Context.MODE_PRIVATE
    ) : ProvideSharedPreferences {
        override fun sharedPreferences(): SharedPreferences =
            context.getSharedPreferences("YouDriveApp", visibility)
    }


}