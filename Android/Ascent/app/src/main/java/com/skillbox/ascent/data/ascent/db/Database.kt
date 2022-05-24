package com.skillbox.ascent.data.ascent.db

import android.content.Context
import androidx.room.Room

object Database {

    lateinit var instance: AscentDatabase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            AscentDatabase::class.java,
            AscentDatabase.DB_NAME
        ).build()
    }
}