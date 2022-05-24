package com.skillbox.ascent.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReminderWorker(val context: Context, val params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private lateinit var listOfDates : List<Long>

    override suspend fun doWork(): Result {
        Log.d("ReminderWork", "Work executing")

        try {
            listOfDates = getDatesOfActivities()
            Log.d("ReminderWork", "list of dates = $listOfDates")
        } catch (t : Throwable) {
            return Result.retry()
        }
        if (listOfDates.isEmpty()) return Result.failure()

        val lastActivityTimeExceeded = dateExceeded(listOfDates.first())

        Log.d("ReminderWork", "last activity time exceeded = $lastActivityTimeExceeded")
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
            AscentDatabase.getInstance(context).ascentActivityDao().getDateList()
        }

    }

    private fun dateExceeded(lastDateOfActivity: Long): Boolean {
        val currentTimeInSec = System.currentTimeMillis() / 1000L
        Log.d("ReminderWork", "current time in sec = $currentTimeInSec")
        val lastActivityDateTime = lastDateOfActivity / 1000L
        Log.d("ReminderWork", "last activity time in sec = $lastActivityDateTime")

        //two days
        return currentTimeInSec - lastActivityDateTime >= 172800
    }

}

