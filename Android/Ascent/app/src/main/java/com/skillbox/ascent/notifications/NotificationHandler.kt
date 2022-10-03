package com.skillbox.ascent.notifications

import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.skillbox.ascent.R
import javax.inject.Singleton

@Singleton
class NotificationHandler {


    fun createNotification(title: String, message: String, context: Context) {

        val builder: NotificationCompat.Builder

        val deepLinkIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(ACTIVITIES_URI))
        deepLinkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        deepLinkIntent.putExtra(INTENT_TAG, INTENT_TYPE)

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            deepLinkIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createNotificationChannel(context)
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mini_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setPriority(VISIBILITY_PUBLIC)

            NotificationManagerCompat
                .from(context)
                .notify(NOTIFICATION_ID, builder.build())

        } else {
            builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_mini_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setPriority(VISIBILITY_PUBLIC)

            NotificationManagerCompat
                .from(context)
                .notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Training Reminder"
            val channel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        description = DESCRIPTION_CHANNEL
                    }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "Reminder notifications"
        private const val NOTIFICATION_ID = 1
        private const val DESCRIPTION_CHANNEL = "Reminder Channel Description"
        private const val REQUEST_CODE = 123
        private const val INTENT_TAG = "intent_tag"
        private const val INTENT_TYPE = "notification"
        private const val ACTIVITIES_URI = "ascent://com.skillbox.ascent/activities"

    }
}

