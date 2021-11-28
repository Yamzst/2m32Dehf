package com.testlabx.mashle.helpers
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
import com.testlabx.mashle.App
import com.testlabx.mashle.MainActivity
import com.testlabx.mashle.R
import com.testlabx.mashle.service.MainService
import com.testlabx.mashle.utils.Varss


class MediaNotificationManager {


    private val mPlayAction: NotificationCompat.Action
    private val mPauseAction: NotificationCompat.Action


    val notificationManager: NotificationManager = App.AppContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        mPlayAction = NotificationCompat.Action(
            R.drawable.ic_play,
            "PLAY",
            createContentIntentToPlayFromNotification()
        )

        mPauseAction = NotificationCompat.Action(
            R.drawable.ic_pause,
            "PAUSE",
            createContentIntentToPauseFromNotification()
        )

        notificationManager.cancelAll()
    }

    fun onDestroy() {}

    fun updateNotification(notificationID: Int, notification: Notification) {
        notificationManager.notify(notificationID, notification)
    }

    fun getMainNotification(isPlaying: Boolean?, token: MediaSessionCompat.Token?, icon: Bitmap?): Notification {
        val builder = buildMainNotification(token, isPlaying!!, icon)
        return builder.build()
    }


    private fun buildMainNotification(token: MediaSessionCompat.Token?, isPlaying: Boolean, icon: Bitmap?
    ): NotificationCompat.Builder {

        val builder = NotificationCompat.Builder(App.AppContext, Constants.PLAYER_CHANNEL_ID)

        if(token!=null){
            val style = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(token)
            style.setShowActionsInCompactView(0)

            builder.setStyle(style)
        }
        else{
            builder.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
            )
        }


        builder.setColorized(true)
        builder.setSmallIcon(R.drawable.ic_notify)
            .setContentIntent(createContentIntentReturnToOpenWindowFromNotificationNew())
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(false)


        builder.setContentTitle(HtmlCompat.fromHtml("<b>" + Varss.dataTitle + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setContentText(Varss.dataChannel)


        if(icon!=null){
            builder.setLargeIcon(icon)
        }


        Log.i("tsNotifyx","Actionnn $isPlaying")
        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)

        return builder
    }



    private fun createContentIntentToPlayFromNotification(): PendingIntent {
        val openUI = Intent(App.AppContext, MainService::class.java)
        openUI.putExtra(
            Constants.serivceNotificationMethod,
            Constants.PLAY_VIDEO_IF_PAUSED_FROM_NOTIFICATION
        )
        return PendingIntent.getService(
            App.AppContext,
            Constants.PLAY_VIDEO_IF_PAUSED_FROM_NOTIFICATION_REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createContentIntentToPauseFromNotification(): PendingIntent {
        val openUI = Intent(App.AppContext, MainService::class.java)
        openUI.putExtra(
            Constants.serivceNotificationMethod,
            Constants.PAUSE_VIDEO_IF_PLAYING_FROM_NOTIFICATION
        )
        return PendingIntent.getService(
            App.AppContext,
            Constants.PAUSE_VIDEO_IF_PLAYING_FROM_NOTIFICATION_REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    /*fun createContentIntentToStopFromNotification(): PendingIntent {
        val openUI = Intent(App.AppContext, MainService::class.java)
        openUI.putExtra(
            Constants.serivceNotificationMethod,
            Constants.STOP_FROM_NOTIFICATION
        )
        return PendingIntent.getService(
            App.AppContext,
            Constants.STOP_FROM_NOTIFICATION_REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }*/


    private fun createContentIntentReturnToOpenWindowFromNotificationNewBC(): PendingIntent {
        val openUI = Intent(App.AppContext, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        openUI.putExtra("OpenUI","codexdt")
        return PendingIntent.getActivity(App.AppContext, Constants.OPEN_REQUEST_CODE, openUI, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun createContentIntentReturnToOpenWindowFromNotificationNew(): PendingIntent{
        val openUI= Intent(App.AppContext, MainActivity::class.java)
        openUI.action = Intent.ACTION_MAIN
        openUI.addCategory(Intent.CATEGORY_LAUNCHER)
        openUI.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        openUI.putExtra("name","redox")  //puede enviar lo que desee a MainActivity
        return PendingIntent.getActivity(App.AppContext,Constants.OPEN_REQUEST_CODE,openUI,PendingIntent.FLAG_UPDATE_CURRENT)

    }


    fun doesNotificationExist(notificationId: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //var notifications = arrayOfNulls<StatusBarNotification>(0)
            val notifications = notificationManager.activeNotifications

            for (notification in notifications) {
                if (notification?.id == notificationId) {
                    return true
                }
            }
        }
        return false
    }
}