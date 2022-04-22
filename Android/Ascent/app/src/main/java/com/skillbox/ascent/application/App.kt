package com.skillbox.ascent.application

import android.app.Application
import androidx.room.Database
import androidx.viewbinding.BuildConfig
import com.bumptech.glide.Glide.init
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }




    }
}