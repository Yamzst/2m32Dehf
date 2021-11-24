package com.testlabx.mashle


import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import java.lang.ref.WeakReference

class App : Application() {


    companion object {

        private var mainActivity: WeakReference<MainActivity>? = null

        lateinit var AppContext: Context
        lateinit var audioManager: AudioManager
        lateinit var pm: PowerManager
        lateinit var wm: WifiManager


        var mWakeLock: PowerManager.WakeLock? = null
        var mWiFiLock: WifiManager.WifiLock? = null


        fun getMainActivity(): MainActivity? {
            return mainActivity?.get()
        }

        fun releaseAndReAcquireLocks(reaquire:Boolean = true) {
            Log.i("locksdx",reaquire.toString())
            
            if(mWakeLock?.isHeld == true){
                mWakeLock?.release()
            }
            if(mWiFiLock?.isHeld == true){
                mWiFiLock?.release()
            }
            if(reaquire){
                mWakeLock?.acquire(600000)
                mWiFiLock?.acquire()
            }
        }
    }


    override fun onTerminate() {
        super.onTerminate()
        releaseAndReAcquireLocks(false)
    }


    override fun onCreate() {
        super.onCreate()
        AppContext = this

        audioManager = AppContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wm = getSystemService(Context.WIFI_SERVICE) as WifiManager

        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
        mWiFiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, javaClass.name)

        releaseAndReAcquireLocks()


        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is MainActivity) {
                    mainActivity = WeakReference(activity)
                }
            }
            override fun onActivityDestroyed(activity: Activity) {
                if (activity is MainActivity) {
                    mainActivity?.clear()
                }
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                releaseAndReAcquireLocks()
            }
            override fun onActivityPaused(activity: Activity) {
                releaseAndReAcquireLocks()
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })


    }
}
