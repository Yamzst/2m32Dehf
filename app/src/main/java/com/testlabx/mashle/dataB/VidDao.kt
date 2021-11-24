package com.testlabx.mashle.dataB

import androidx.room.*

@Dao
interface VidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVid(vid: Vid): Long

    @Query("UPDATE vid SET urlVid= :urlVidx,urlAud=:urlAudx where idVid = :id")
    fun setUrlVid(id: String,urlVidx: String,urlAudx: String)

    //SAVE
    @Query("UPDATE vid SET svVid= :uriSv where idVid = :id")
    fun setSvVid(id: String,uriSv: String)

    @Query("select svVid from vid where idVid = :id")
    fun getSvVid(id: String):String

    @Query("UPDATE vid SET posVid= :newPos where idVid = :id")
    fun setPos(id: String,newPos: Int)

    @Query("UPDATE vid SET svAud= :uriSv where idVid = :id")
    fun setSvAud(id: String,uriSv: String)

    @Query("select svAud from vid where idVid = :id")
    fun getSvAud(id: String):String



    @Query("select * from vid")
    fun getAllUser():List<Vid>

    @Query("select idVid from vid")
    fun getAllIds():List<String>

    @Query("select urlVid from vid where idVid = :id")
    fun getUrlVid(id: String):String

    @Query("select * from vid where idVid = :id ")
    fun getVidFromId(id:String):Vid

    @Query("select nmVid from vid where idVid = :id ")
    fun getNmFromId(id:String):String

    @Query("select chVid from vid where idVid = :id ")
    fun getChFromId(id:String):String


    @Query("select nmVid from vid where posVid = :pos ")
    fun getNmFromPos(pos:Int):String

    @Query("select chVid from vid where posVid = :pos ")
    fun getChFromPos(pos:Int):String


    @Query("select * from vid where posVid = :pos")
    fun getVidFromPos(pos:Int):Vid

    @Query("select urlVid from vid where posVid = :pos")
    fun getUrlFromPos(pos:Int):String

    @Query("DELETE from vid")
    fun deleteAll()

    @Query("DELETE from vid where idVid = :id")
    fun deleteId(id:String)


    /*
       @Query("DELETE from user where idVid = :id")
    fun deleteInfoVid(id: String)
     */


}