package com.testlabx.mashle.utils

import android.content.Context
import com.testlabx.mashle.dataB.AppDatabase
import com.testlabx.mashle.dataB.Vid

object DtList {

    var database: AppDatabase? = null

    fun initDataBase(context:Context){
        database = AppDatabase.getDatabase(context)
    }

    fun setNewVid(vid:Vid){
        database!!.vidDao().insertVid(vid)
    }

    fun rmvId(id:String){
        database!!.vidDao().deleteId(id)
    }


    fun getVidFromId(id:String): Vid {
        return  database!!.vidDao().getVidFromId(id)
    }

    fun getVidFromPos(pos:Int): Vid {
        return database!!.vidDao().getVidFromPos(pos)
    }


    fun setSvVid(id:String,uriSv:String){
        database!!.vidDao().setSvVid(id,uriSv)
    }

    fun getSvVid(id:String):String{
        return database!!.vidDao().getSvVid(id)
    }


    fun setSvAud(id:String,uriSv:String){
        database!!.vidDao().setSvAud(id,uriSv)
    }

    fun getSvAud(id:String):String{
        return database!!.vidDao().getSvAud(id)
    }



}