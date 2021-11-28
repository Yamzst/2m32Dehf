package com.testlabx.mashle.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import com.testlabx.mashle.App
import com.testlabx.mashle.helpers.Constants
import com.testlabx.mashle.helpers.FirebaseRC
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object Utilsx {



   fun getQrsFromSt(st1:String,st2:String,st3:String,qrs:ArrayList<PlsMn>) {

       splitString(st1,qrs)
       splitString(st2,qrs)
       splitString(st3,qrs)

    }


    fun splitString(st:String,qrs:ArrayList<PlsMn>){
        if (st.contains(" /--/ ")){

            val pSt = st.split(" /--/ ").toTypedArray()
            val nwPls = PlsMn(pSt[0],pSt[1])
            qrs.add(nwPls)

        }

    }


    fun createNotificationChannel(ctn:Context,chId:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(chId, "New Music", importance)

            val manager: NotificationManager =
                ctn.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun createNtfChPlayer(ctn:Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager = ctn.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (manager.getNotificationChannel(Constants.PLAYER_CHANNEL_ID) == null) {

                val importance = NotificationManager.IMPORTANCE_LOW

                val channel = NotificationChannel(Constants.PLAYER_CHANNEL_ID, "Player", importance)

                channel.description = "Control Player"

                manager.createNotificationChannel(channel)

            }
        }
    }




    fun cleanTitle(tl:String):String{
        var newTl = if (tl.contains("(")){
            tl.replace("\\(.*\\)".toRegex(), "").trim()
        }else{
            tl
        }
        return newTl
    }


    fun getIdFromLink(link: String): String {
        return link.run {
            substring(lastIndexOf("=") + 1)
        }
    }


    fun nmRandom(lenght: Int): String {
        val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return alphaNumeric.shuffled().take(lenght).joinToString("")
    }


    fun prosNmVid(tl:String,ch:String){
        if (tl.contains(" - ")){
            val strs = tl.split(" - ").toTypedArray()

            Varss.dataChannel = strs[0]

            if (strs[1].contains("(")){
                Varss.dataTitle = strs[1].replace("\\(.*\\)".toRegex(), "").trim()
            }else{
                Varss.dataTitle = strs[1].replace("\\[.*\\]".toRegex(), "").trim()
            }

        }else{
            Varss.dataChannel = ch
            Varss.dataTitle = tl
        }
    }




}