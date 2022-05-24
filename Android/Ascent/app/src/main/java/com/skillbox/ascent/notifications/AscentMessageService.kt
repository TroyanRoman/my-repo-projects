package com.skillbox.ascent.notifications

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AscentMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(PUSH_INTENT_FILTER)
        message.data.forEach { entity ->
            intent.putExtra(entity.key, entity.value)
            Log.d("FirebaseLog", "entity key sent = ${entity.key}, value = ${entity.value} ")
        }

     sendBroadcast(intent)
    }

    companion object {
        const val PUSH_INTENT_FILTER = "PUSH_EVENT"
        const val KEY_ACTION = "action"
        const val KEY_MESSGAE = "message"
        const val ACTION_SHOW_MESSAGE = "show_message"
    }
}