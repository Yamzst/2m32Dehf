package com.testlabx.mashle.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper


object NotifyDwnld {
    fun sendStatusNotification(title: String, context: Context, notificationId: Int,img:Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download"
            val description = "Download Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channelDwnld", name, importance)
            channel.description = description

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        /*val builder = NotificationCompat.Builder(context, "channelDwnld")
            .setSmallIcon(R.drawable.ic_app_fg)
            .setContentTitle(title)
                //if es audio
            .setLargeIcon(img)
            .setContentInfo("\uD83C\uDFB6 Descarga Finalizada \uD83C\uDFB6")
            .setContentText("\uD83C\uDFB6 Descarga Finalizada \uD83C\uDFB6")
            .setColor(Color.YELLOW)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())

        Handler(Looper.getMainLooper()).postDelayed({
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.cancel(notificationId)
        }, 5000)*/




    }
}