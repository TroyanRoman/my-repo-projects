package com.skillbox.ascent.data.ascent.repositories.user

import android.content.Context
import androidx.work.*
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.di.NotificationPrefs
import com.skillbox.ascent.di.UserDataPreferences

import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.notifications.ReminderWorker
import com.skillbox.ascent.oauth_data.AuthConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ascentUserApi: AscentUserApi,
    private val userDataPrefs: UserDataPreferences,
    private val notificationPrefs: NotificationPrefs,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
) : UserRepository {

    private val userFullName = userDataPrefs.getRequiredUser(AuthConfig.FULL_NAME_KEY)

    private val isMessageShown = notificationPrefs.isMessageShown()

    override suspend fun getUserIdentity(): AscentUser {
        return withContext(contextDispatcher) {
            ascentUserApi.getAuthenticatedUser()
        }
    }

    override suspend fun showNotificationMessage() {

        if(!isMessageShown) {
            createWorkRequest()
            notificationPrefs.setIsMessageShown(true)
        }

    }


    private fun createWorkRequest() {
        val notificationWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(8, TimeUnit.HOURS)
            .setInputData(
                workDataOf(
                    "title" to "Ascent training reminder",
                    "message" to "Hello, $userFullName, it's time to get back in shape! Your last activity happened more then two days ago"
                )
            ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork( "notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWorkRequest)

    }

    override fun storeUserData(user: AscentUser) {
        userDataPrefs.setStoredUser(user)
    }





    override suspend fun updateUserIdentity(weight: Float) {
        withContext(contextDispatcher) {
            ascentUserApi.updateUser(weight)
        }
    }



}