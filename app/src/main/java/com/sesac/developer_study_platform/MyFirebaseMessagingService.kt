package com.sesac.developer_study_platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sesac.developer_study_platform.data.source.local.FcmTokenRepository
import com.sesac.developer_study_platform.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val fcmTokenRepository = FcmTokenRepository(this)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            fcmTokenRepository.setToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        kotlin.runCatching {
            if (remoteMessage.data.isNotEmpty()) {
                val uid = remoteMessage.data.getValue("uid")

                createNotificationChannel()
                if (uid != Firebase.auth.uid) {
                    sendNotification(remoteMessage.data)
                }
            }
        }.onFailure {
            Log.e("MyFirebaseMessagingService-onMessageReceived", it.message ?: "error occurred.")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.default_notification_channel_id)
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(data: Map<String, String>) {
        kotlin.runCatching {
            val randomNumber = Random.nextInt()
            val builder = getNotificationBuilder(data, randomNumber)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(randomNumber, builder.build())
        }.onFailure {
            Log.e("MyFirebaseMessagingService-sendNotification", it.message ?: "error occurred.")
        }
    }

    private fun getNotificationBuilder(
        data: Map<String, String>,
        randomNumber: Int
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(getLargeIcon(data.getValue("imageUrl")))
            .setColor(getColor(R.color.white))
            .setContentTitle(data.getValue("title"))
            .setContentText(data.getValue("text"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent(data, randomNumber))
            .setAutoCancel(true)
    }

    private fun getLargeIcon(imageUrl: String): Bitmap {
        return Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .submit()
            .get()
    }

    private fun getPendingIntent(data: Map<String, String>, randomNumber: Int): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("sid", data.getValue("sid"))
        }
        return PendingIntent.getActivity(this, randomNumber, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}