package com.skillbox.ascent.data.ascent.repositories.user

import android.content.Context
import androidx.work.*
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.di.preferences.NotificationPrefs
import com.skillbox.ascent.di.preferences.UserDataPreferences
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.notifications.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    private val isMessageShown = notificationPrefs.isMessageShown()

    override suspend fun getUserIdentity(): Flow<AscentUser> {
        return withContext(contextDispatcher) {
            flowOf(ascentUserApi.getAuthenticatedUser())
        }
    }


    override suspend fun showNotificationMessage() {

        if (!isMessageShown) {
            createWorkRequest()
            notificationPrefs.setIsMessageShown(true)
        }

    }


    private fun createWorkRequest() {
        val userFullName =
            userDataPrefs.getRequiredUser(UserDataPreferences.FULL_NAME_KEY)

        val notificationWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(HOURS_IN_DAY, TimeUnit.HOURS)
            .setInputData(
                workDataOf(
                    "title" to "Ascent training reminder",
                    "message" to "Hello, $userFullName, it's time to get back in shape! Your last activity happened more then two days ago"
                )
            ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWorkRequest
        )


    }

    override fun storeUserData(user: AscentUser) {
        userDataPrefs.setStoredUser(user)
    }
    

    override suspend fun updateUserIdentity(weight: Float) {
        withContext(contextDispatcher) {
            ascentUserApi.updateUser(weight)
        }
    }

    companion object {
        const val HOURS_IN_DAY = 24L
    }


}