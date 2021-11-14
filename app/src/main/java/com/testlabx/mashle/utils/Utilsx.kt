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
import com.testlabx.mashle.App
import com.testlabx.mashle.helpers.FirebaseRC
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object Utilsx {



   fun getQrsFromSt(st1:String,st2:String,st3:String,qrs:ArrayList<PlsMn>) {

       splitString(st1,qrs)
       splitString(st2,qrs)
       splitString(st3,qrs)


       //ADD
       //IF ADDS ESTAN ACTIVADOS
       /*if (FirebaseRC.dsNtv != 1 && FirebaseRC.dsNtv != 3){
           qrs.add(PlsMn("","",""))
       }*/


    }


    fun splitString(st:String,qrs:ArrayList<PlsMn>){
        if (st.contains(" /--/ ")){
            val pSt = st.split(" /--/ ").toTypedArray()

            val nwPls = PlsMn(pSt[0],pSt[1])
            qrs.add(nwPls)

        }

    }


    fun dayOfWeek(): Int {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        println(
            when (day) {
                1 -> "Sunday"
                2 -> "Monday"
                3 -> "Tuesday"
                4 -> "Wednesday"
                5 -> "Thursday"
                6 -> "Friday"
                7 -> "Saturday"
                else -> "Time has stopped"
            }
        )
        return day
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


    //dw

    fun createFile(context: Context,tp:String,nm:String):File{
        val tmpFileDw = File.createTempFile("vid", null, context.applicationContext.externalCacheDir)
        tmpFileDw.delete()
        tmpFileDw.mkdir()
        tmpFileDw.deleteOnExit()
        //hasta aui folder
        ///Log.i(TAG, tmpFileDw.absolutePath)
        val dwFile =  if (tp == "video"){
            File(tmpFileDw, "$nm.mp4")
        }else{
            File(tmpFileDw, "$nm.m4a")
        }

        return dwFile

    }


    fun nmRandom(lenght: Int): String {
        val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return alphaNumeric.shuffled().take(lenght).joinToString("")
    }






}