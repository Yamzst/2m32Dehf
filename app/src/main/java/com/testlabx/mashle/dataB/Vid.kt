package com.testlabx.mashle.dataB

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vid")
data class Vid(
   //vid

   //@PrimaryKey
   //@PrimaryKey(autoGenerate = true)
   //@ColumnInfo(name = "id")
   //var itmId : Int = 0,
   @PrimaryKey
   @ColumnInfo(name = "idVid")
   var idVid: String = "",

   @ColumnInfo(name = "posVid")
   var posVid: Int = 0,

   @ColumnInfo(name = "nmVid")
   var nmVid: String = "",

   @ColumnInfo(name = "chVid")
   var chVid: String = "",

   @ColumnInfo(name = "urlVid")
   var urlVid: String = "",

   @ColumnInfo(name = "urlAud")
   var urlAud: String = "",

   @ColumnInfo(name = "svVid")
   var svVid:  String = "",

   @ColumnInfo(name = "svAud")
   var svAud:  String = ""

) {
}