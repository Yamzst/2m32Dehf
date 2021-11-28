package com.testlabx.mashle.utils


import android.graphics.Color
import android.util.Log
import java.util.ArrayList


class Varss {

    init {
        Log.i("TestRmt","gclas Varssssssssss")
    }
    companion object{

        //Al init set vars de restartVars no estas
        var currentId = ""

        var dataTitle = "Mashle"

        var dataChannel = "Listo para descubrir ✨"

        var pdAdWeb = false

        var dwCount = 0

        var qr = ""

        var frstDw = true

        var crntFg = 0

        var crtnPlst = 0

        var mnList: ArrayList<PlsMn> = ArrayList()

        var stBfAd = false

        fun restartVars(){

            currentId = ""

            dataTitle = "Mashle"

            dataChannel = "Listo para descubrir ✨"

            pdAdWeb = false

            dwCount = 0

            qr = ""

            frstDw = true

            crntFg = 0

            crtnPlst = 0

            mnList.clear()

            stBfAd = false

        }
    }
}