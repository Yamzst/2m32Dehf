package com.testlabx.mashle.utils


import android.graphics.Color
import android.util.Log
import java.util.ArrayList


class Varss {

    init {
        Log.i("TestRmt","gclas Varssssssssss")
    }
    companion object{

        var pdAdWeb = false

        var dwCount = 0

        var qr = ""

        var frstDw = true

        var idsN = 0

        var crntFg = 0

        var crtnPlst = 0

        var mnList: ArrayList<PlsMn> = ArrayList()

        var stBfAd = false

        fun restartVars(){
            pdAdWeb = false

            crtnPlst = 0

            dwCount = 0

            qr = ""

            frstDw = true

            idsN = 0

            crntFg = 0

            mnList.clear()

            stBfAd = false

        }
    }
}