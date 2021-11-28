package com.testlabx.mashle.service


import android.app.Notification
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.testlabx.mashle.App
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.Constants
import com.testlabx.mashle.helpers.MediaNotificationManager
import com.testlabx.mashle.utils.Varss
import kotlin.system.exitProcess
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside


class MainService : MediaBrowserServiceCompat() {
    private var serviceStartId = -1

    private fun releaseServiceUiAndStop() {
        stopSelf(serviceStartId)
        stopForeground(true)
    }

    var emptyNotificationBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    private var originalVideoImageId = ""
    private var orignalVideoId = "-1"
    private var orignalIsPlaying = false
    private lateinit var mMediaNotificationManager: MediaNotificationManager
    private var token: MediaSessionCompat.Token? = null

    private var originalVideoImage: Bitmap? = null

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return null
    }

    override fun onLoadChildren(s: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceStartId = startId
        if (intent != null) {
            val methodName = intent.getStringExtra(Constants.serivceNotificationMethod)
            if (methodName != null) {
                when (methodName) {
                    Constants.PAUSE_VIDEO_IF_PLAYING_FROM_NOTIFICATION -> {
                        val serviceToMainIntent = Intent(Constants.SERVICE_TO_MAIN_ACTIVITY_CALLBACK_ID)
                        serviceToMainIntent.putExtra(
                            Constants.serivceNotificationMethod,
                            methodName
                        )
                        sendBroadcast(serviceToMainIntent)
                    }
                    Constants.PLAY_VIDEO_IF_PAUSED_FROM_NOTIFICATION -> {
                        val serviceToMainIntent = Intent(Constants.SERVICE_TO_MAIN_ACTIVITY_CALLBACK_ID)
                        serviceToMainIntent.putExtra(
                            Constants.serivceNotificationMethod,
                            methodName
                        )
                        sendBroadcast(serviceToMainIntent)
                    }


                    Constants.STOP_FROM_NOTIFICATION -> {
                        stop()
                    }
                    Constants.UPDATE_NOTIFICATION -> {

                        if (App.getMainActivity()?.simpleExoPlayer != null) {
                            setupNotification(
                                App.getMainActivity()?.simpleExoPlayer!!.isPlaying,
                                Varss.currentId
                            )
                        }

                    }
                }
            }
        }
        return if(intent?.getStringExtra(Constants.serivceNotificationMethod) == Constants.STOP_FROM_NOTIFICATION){
            Service.START_NOT_STICKY
        } else{
            START_STICKY
        }
    }


    private var mediaSession: MediaSessionCompat? = null


    override fun onCreate() {
        super.onCreate()

        emptyNotificationBitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_ntf)

        mediaSession = MediaSessionCompat(this, App.AppContext.getString(R.string.app_name), null, null)
        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPause() {
                App.getMainActivity()?.pauseVideo()
            }

            override fun onPlay() {
                App.getMainActivity()?.playVideo()
            }

            override fun onStop() {}

            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                handleMediaButton(mediaButtonEvent)
                return true
            }

        })


        token = mediaSession?.sessionToken

        mMediaNotificationManager = MediaNotificationManager()

        setupNotification(false, "")
    }

    private fun setupNotification(
        setupNotificationisPlaying: Boolean,
        setupNotificationid: String,
    ) {

        Log.i("tsStx","oncreate ${Varss.currentId}")
        token?.let {
            if (orignalIsPlaying != setupNotificationisPlaying || orignalVideoId != setupNotificationid) {
                if (::mMediaNotificationManager.isInitialized) {
                    updateNotification(mMediaNotificationManager, setupNotificationisPlaying, it, setupNotificationid)
                }
                orignalIsPlaying = setupNotificationisPlaying
                orignalVideoId = setupNotificationid
            }
        }

    }

    private fun updateNotification(
        mMediaNotificationManagerAsyncTask: MediaNotificationManager,
        updateNotificationAsyncTaskisPlaying: Boolean,
        updateNotificationAsyncTaskmediaSessionCompattoken: MediaSessionCompat.Token,
        updateNotificationAsyncTaskid: String
    ) {


        Log.i("tsNotifyx","Actionnn updateNotification $updateNotificationAsyncTaskisPlaying")

        Log.i("tsStx","updateNotifi ${Varss.currentId}")
        if (updateNotificationAsyncTaskid.isNotEmpty()) {
            if (originalVideoImageId != updateNotificationAsyncTaskid) {
                val thumbnailImageURL = "https://img.youtube.com/vi/>>VIDEO_ID<</mqdefault.jpg".replace(">>VIDEO_ID<<", updateNotificationAsyncTaskid)

                Glide.with(App.AppContext)
                    .asBitmap()
                    .load(thumbnailImageURL)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(320, 320) //240 y 135
                    .transform(CenterCrop())
                    //.transform(CenterCrop(),RoundedCorners(25))
                    .dontAnimate()
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            originalVideoImage = resource
                            originalVideoImageId = updateNotificationAsyncTaskid
                            Log.i("tsNotifyx","Actionnn onResourceReady $updateNotificationAsyncTaskisPlaying")

                            updateNotification2ndStage(mMediaNotificationManagerAsyncTask, updateNotificationAsyncTaskisPlaying, updateNotificationAsyncTaskmediaSessionCompattoken, updateNotificationAsyncTaskid, originalVideoImage!!)

                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}

                        override fun onLoadFailed(errorDrawable: Drawable?) {

                            Log.i("tsNotifyx","Actionnn onFailed $updateNotificationAsyncTaskisPlaying")

                            updateNotification2ndStage(mMediaNotificationManagerAsyncTask, updateNotificationAsyncTaskisPlaying, updateNotificationAsyncTaskmediaSessionCompattoken, updateNotificationAsyncTaskid, emptyNotificationBitmap!!)
                        }
                    })
            }
            else{
                Log.i("tsNotifyx","mismo id$updateNotificationAsyncTaskisPlaying")

                updateNotification2ndStage(mMediaNotificationManagerAsyncTask, updateNotificationAsyncTaskisPlaying, updateNotificationAsyncTaskmediaSessionCompattoken, updateNotificationAsyncTaskid, originalVideoImage!!)
            }
        }
        else{
            Log.i("tsNotifyx","Actionnn enmpty$updateNotificationAsyncTaskisPlaying")

            updateNotification2ndStage(mMediaNotificationManagerAsyncTask, false, updateNotificationAsyncTaskmediaSessionCompattoken, "",  emptyNotificationBitmap!!)
        }
    }

    private fun updateNotification2ndStage(
        mMediaNotificationManagerAsyncTask: MediaNotificationManager,
        updateNotificationAsyncTaskisPlaying: Boolean,
        updateNotificationAsyncTaskmediaSessionCompattoken: MediaSessionCompat.Token,
        updateNotificationAsyncTaskid: String,
        icon: Bitmap
    ) {

        Log.i("tsNotifyx","Actionnn ${Varss.dataTitle} de updateNotification2ndStage $updateNotificationAsyncTaskisPlaying")

        val notification = mMediaNotificationManagerAsyncTask.getMainNotification(
            updateNotificationAsyncTaskisPlaying,
            updateNotificationAsyncTaskmediaSessionCompattoken,
            icon
        )
        notification.flags = Notification.FLAG_ONGOING_EVENT or Notification.FLAG_FOREGROUND_SERVICE

        if (mMediaNotificationManagerAsyncTask.doesNotificationExist(Constants.MAIN_NOTIFICATION_ID)) {
            mMediaNotificationManagerAsyncTask.updateNotification(
                Constants.MAIN_NOTIFICATION_ID,
                notification
            )
        } else {
            startForeground(Constants.MAIN_NOTIFICATION_ID, notification)
        }


        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()

                .putString(MediaMetadata.METADATA_KEY_TITLE, Varss.dataTitle)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, Varss.dataChannel)
                .putBitmap(MediaMetadata.METADATA_KEY_ART, icon)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                .build()
        )


        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(if (updateNotificationAsyncTaskisPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED, 1, 1.0f,  SystemClock.elapsedRealtime())
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SEEK_TO or
                            PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build()
        )

        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession?.isActive = updateNotificationAsyncTaskisPlaying
    }






    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    private fun stop() {
        App.getMainActivity()?.pauseVideo()
        releaseServiceUiAndStop()

        mediaSession?.release()

        App.releaseAndReAcquireLocks(false)

        if (::mMediaNotificationManager.isInitialized) {
            mMediaNotificationManager.notificationManager.cancelAll()
            mMediaNotificationManager.onDestroy()
        }

        (App.audioManager).unregisterMediaButtonEventReceiver(
            ComponentName(
                packageName, MediaButtonReceiver::class.java.name
            )
        )

        stopForeground(true)
        stopSelf()

        App.getMainActivity()?.finish()

        exitProcess(0)
    }


    private fun hndlPlayPause(){
        App.getMainActivity()?.simpleExoPlayer!!.playWhenReady = !App.getMainActivity()?.simpleExoPlayer!!.playWhenReady
    }

    private fun handleMediaButton(mediaButtonEvent: Intent) {
        if (mediaButtonEvent.action == Intent.ACTION_MEDIA_BUTTON) {
            val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (event != null) {
                if (event.action == KeyEvent.ACTION_UP) {
                    when (event.keyCode) {

                        KeyEvent.KEYCODE_MEDIA_PLAY -> Log.i("eventHd","play")
                        KeyEvent.KEYCODE_MEDIA_PAUSE -> Log.i("eventHd","pause")
                        KeyEvent.KEYCODE_MEDIA_STOP -> Log.i("eventHd","stop")
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> hndlPlayPause()

                    }
                }
            }
        }
    }


}