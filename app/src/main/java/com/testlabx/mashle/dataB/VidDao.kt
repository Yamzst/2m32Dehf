package com.testlabx.mashle.dataB

import androidx.room.*

@Dao
interface VidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVid(vid: Vid): Long

    @Query("select * from vid where idVid = :id ")
    fun getVidFromId(id:String):Vid

    @Query("select * from vid where posVid = :pos")
    fun getVidFromPos(pos:Int):Vid


    //SvAud - SvVid
    @Query("UPDATE vid SET svVid= :uriSv where idVid = :id")
    fun setSvVid(id: String,uriSv: String)

    @Query("select svVid from vid where idVid = :id")
    fun getSvVid(id: String):String


    @Query("UPDATE vid SET svAud= :uriSv where idVid = :id")
    fun setSvAud(id: String,uriSv: String)

    @Query("select svAud from vid where idVid = :id")
    fun getSvAud(id: String):String


    /*@Query("select * from vid")
    fun getAllUser():List<Vid>

    @Query("select idVid from vid")
    fun getAllIds():List<String>
    */


    @Query("DELETE from vid")
    fun deleteAll()

    @Query("DELETE from vid where idVid = :id")
    fun deleteId(id:String)



}