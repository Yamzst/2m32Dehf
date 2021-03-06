package com.testlabx.mashle.utils

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.testlabx.mashle.App
import com.testlabx.mashle.MainActivity
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.Constants
import android.animation.AnimatorListenerAdapter
import android.app.Notification
import android.graphics.Color

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


fun Context.openBrowser(urlForBrowser:String){
    if (urlForBrowser != ""){
        val intentNavegador = Intent(Intent.ACTION_VIEW, Uri.parse(urlForBrowser))
        startActivity(intentNavegador)
    }


}

fun Context.uiMode():Boolean{
    return when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED ->false
        else -> false
    }

}


fun Activity.tintUi(){
    if (!uiMode()){
        WindowInsetsControllerCompat(this.window, window.decorView).isAppearanceLightStatusBars = true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        /*window.apply {
            statusBarColor = getColorx(R.color.tssc)
            navigationBarColor = getColorx(R.color.tssc)
        }*/
        //tintBarSt(getColorx(R.color.tssc))
        //tintBarNv(getColorx(R.color.tssc))
    }else{
        /*window.apply {
            statusBarColor = getColorx(R.color.black)
            navigationBarColor = getColorx(R.color.black)
        }*/
        tintBarSt(getColorx(R.color.black))
        tintBarNv(getColorx(R.color.black))


    }
}



fun Activity.tintBarSt(@ColorInt color:Int){
    window.apply { statusBarColor = color  }
}

fun Activity.tintBarNv(@ColorInt color:Int){
    window.apply { navigationBarColor = color }
}


fun Activity.tintBarsTransparent(){
    window.apply {
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = ContextCompat.getColor(this@tintBarsTransparent,R.color.semi_transparent)
    }
}


fun Activity.getColorx(@ColorRes color:Int) = ContextCompat.getColor(this, color)


//NOTIFIY
fun Context.getBtmGld(tlNt: String, ctnNt: String, dtNt: String, imgNt: String, type: String) {
    Glide.with(this)
        .asBitmap()
        .load(imgNt)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .override(240, 135)
        .dontAnimate()
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                showNotify(tlNt, ctnNt, dtNt, resource, type,Constants.CHANNELID)
            }
            override fun onLoadCleared(placeholder: Drawable?) {}

            override fun onLoadFailed(errorDrawable: Drawable?) {
                val img = BitmapFactory.decodeResource(resources,R.drawable.ic_ntf)

                showNotify(tlNt, ctnNt, dtNt, img, type,Constants.CHANNELID)
            }
        })

}


fun Context.showNotify(tlNtf: String, ctnNtf: String, dtNtf: String, imgNtf: Bitmap, type: String,chId:String) {
    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        putExtra(type, dtNtf)
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val contentIntent = PendingIntent.getActivity(
        applicationContext,
        714,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notification = NotificationCompat.Builder(this,chId).also {
        it.setContentTitle(tlNtf)
        it.setContentText(ctnNtf)
        it.setContentIntent(contentIntent)
        it.setSmallIcon(R.drawable.ic_notify)
        it.setLargeIcon(imgNtf)
        it.color = ContextCompat.getColor(this, R.color.color_primary)
        it.setColorized(true)
        it.setAutoCancel(true)
        it.priority = NotificationCompat.PRIORITY_HIGH

    }.build()
    val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
    notificationManager.notify(381, notification)
}


fun Context.sharedApp(url:String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)

}



fun View.AnimView(){
    //setColorFilter(R.color.purple_500)
    //imageTintList = ColorStateList.valueOf(R.color.teal_200)
    animate()
        .alpha(0.7f)
        .setDuration(250)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                alpha = 1f
            }
        })

}

fun Activity.btnHome(){
    val intentNavegador = Intent(Intent.ACTION_MAIN)
    intentNavegador.addCategory(Intent.CATEGORY_HOME)
    //startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intentNavegador)
}


fun Intent.getIntentValue(key: String):Boolean {
    getStringExtra(key).let {
        return if (it != null){
            Intentsx.valuex = it
            true
        }else{
            false
        }
    }


}