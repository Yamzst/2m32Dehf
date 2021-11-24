package com.testlabx.mashle.helpers

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseEvent {

    private var anlts:FirebaseAnalytics? = null

    //Init con context de MAIN ACTIVITY
    fun init(context: Context){
        anlts = FirebaseAnalytics.getInstance(context)
    }

    fun send(nmE:String,key:String,value:String){
        val bundle = Bundle()
        bundle.putString(key, value)
        anlts?.logEvent(nmE, bundle)
    }
}