package com.testlabx.mashle.utils

import android.content.Context
import com.testlabx.mashle.dataB.AppDatabase
import com.testlabx.mashle.dataB.Vid

object DtList {

    var database: AppDatabase? = null

    fun initDataBase(context:Context){
        database = AppDatabase.getDatabase(context)
    }


    fun getNmFromId(id:String): String {
        return  database!!.vidDao().getNmFromId(id)
    }

    fun getChFromPos(pos:Int): String {
        return  database!!.vidDao().getChFromPos(pos)
    }

    fun getNmFromPos(pos:Int): String {
        return  database!!.vidDao().getNmFromPos(pos)
    }

    fun getChFromId(id:String): String {
        return  database!!.vidDao().getChFromId(id)
    }


    fun getVidFromId(id:String): Vid {
        return  database!!.vidDao().getVidFromId(id)
    }

    fun getVidFromPos(pos:Int): Vid {
        return database!!.vidDao().getVidFromPos(pos)
    }

    fun updVid(idNw:String,urlVid:String,urlAud:String){
        database!!.vidDao().setUrlVid(idNw,urlVid,urlAud)
    }

    fun setSvVid(idNw:String,uriSv:String){
        database!!.vidDao().setSvVid(idNw,uriSv)
    }

    fun getSvVid(idNw:String):String{
        return database!!.vidDao().getSvVid(idNw)
    }


    fun setSvAud(idNw:String,uriSv:String){
        database!!.vidDao().setSvAud(idNw,uriSv)
    }

    fun getSvAud(idNw:String):String{
        return database!!.vidDao().getSvAud(idNw)
    }

    fun setPos(idNw:String,newPos:Int){
        database!!.vidDao().setPos(idNw,newPos)
    }
}