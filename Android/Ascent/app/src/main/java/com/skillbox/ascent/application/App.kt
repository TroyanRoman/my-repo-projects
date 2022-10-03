package com.skillbox.ascent.application

import android.app.Application
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.viewbinding.BuildConfig
import com.skillbox.ascent.application.theme_change.AppThemeSelector
import com.skillbox.ascent.data.ascent.db.Database
import com.skillbox.ascent.di.preferences.NotificationPrefs
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), androidx.work.Configuration.Provider {

    @Inject
    lateinit var notificationPrefs: NotificationPrefs

    @Inject
    lateinit var themeSelector: AppThemeSelector

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Database.init(this)
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }
        super.onCreate()

        themeSelector.setAppTheme()
    }

    override fun onTerminate() {
        notificationPrefs.setIsMessageShown(false)
        super.onTerminate()
    }

    override fun getWorkManagerConfiguration(): androidx.work.Configuration =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}