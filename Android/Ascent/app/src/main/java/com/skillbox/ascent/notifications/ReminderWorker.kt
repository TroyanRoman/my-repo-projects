package com.skillbox.ascent.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @ApplicationContext val context: Context,
    @Assisted  val params: WorkerParameters,
    val database: AscentDatabase
) :
    CoroutineWorker(context, params) {

    private lateinit var listOfDates: List<Long>

    override suspend fun doWork(): Result {

        try {
            listOfDates = getDatesOfActivities()
        } catch (t: Throwable) {
            return Result.retry()
        }
        if (listOfDates.isEmpty()) return Result.failure()

        val lastActivityTimeExceeded = dateExceeded(listOfDates.first())

        return if (lastActivityTimeExceeded) {
            NotificationHandler().createNotification(
                inputData.getString("title").toString(),
                inputData.getString("message").toString(),
                context
            )
            Result.success()
        } else Result.failure()
    }


    private suspend fun getDatesOfActivities(): List<Long> {
        return withContext(Dispatchers.IO) {
            //AscentDatabase.getInstance(context).ascentActivityDao().getDateList()
            database.ascentActivityDao().getDateList()
        }

    }

    private fun dateExceeded(lastDateOfActivity: Long): Boolean {
        val currentTimeInSec = System.currentTimeMillis() / MILLIS_IN_SEC

        val lastActivityDateTime = lastDateOfActivity / MILLIS_IN_SEC


        //two days
        return currentTimeInSec - lastActivityDateTime >= TWO_DAYS_IN_SEC
    }

    companion object {
        private const val TWO_DAYS_IN_SEC = 172800
        private const val MILLIS_IN_SEC = 1000L
    }

}

