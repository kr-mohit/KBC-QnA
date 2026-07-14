package com.pramoh.kbcqna.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pramoh.kbcqna.HomeActivity
import com.pramoh.kbcqna.R

class KbcFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_SERVICE", "onMessageReceived called from: ${remoteMessage.from}")

        // Log Data Payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_SERVICE", "Message data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"] ?: "KBC QnA"
            val body = remoteMessage.data["body"] ?: ""
            sendNotification(title, body)
        }

        // Log Notification Payload
        remoteMessage.notification?.let {
            Log.d("FCM_SERVICE", "Message notification payload - Title: ${it.title}, Body: ${it.body}")
            val title = it.title ?: "KBC QnA"
            val body = it.body ?: ""
            sendNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM_SERVICE", "onNewToken called - Refreshed token: $token")
    }

    private fun sendNotification(title: String, messageBody: String) {
        Log.d("FCM_SERVICE", "sendNotification called - Title: $title, Message: $messageBody")
        try {
            val intent = Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            val pendingIntentFlags =
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                pendingIntentFlags
            )

            val channelId = "kbc_general_channel"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "General game notifications and updates"
                }
                notificationManager.createNotificationChannel(channel)
                Log.d("FCM_SERVICE", "Notification channel created or already exists")
            }

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
            Log.d("FCM_SERVICE", "Notification successfully posted with ID: $notificationId")
        } catch (e: Exception) {
            Log.e("FCM_SERVICE", "Error posting notification", e)
        }
    }
}