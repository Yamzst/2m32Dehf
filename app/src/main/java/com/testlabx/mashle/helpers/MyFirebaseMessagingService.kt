package com.testlabx.mashle.helpers

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.testlabx.mashle.App
import com.testlabx.mashle.utils.getBtmGld


class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        var key = ""
        when{
            p0.data.containsKey("ntPlst") ->{
                //key = "ntPlst"
                //si esta en la app no va a recibir playlist
                //
            }
            p0.data.containsKey("ntBasic") ->{
                key = "ntBasic"
            }
            p0.data.containsKey("ntBrwsr") ->{
                key = "ntBrwsr"
            }

        }

        if (key != ""){
           getBtmGld(p0.notification?.title.toString(),
                p0.notification?.body.toString(), p0.data[key].toString(), p0.notification?.imageUrl.toString(),key)
        }

        //super.onMessageReceived(p0)
    }
}