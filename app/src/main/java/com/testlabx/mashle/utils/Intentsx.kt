package com.testlabx.mashle.utils

import android.content.Intent
import android.net.Uri
import com.testlabx.mashle.App

object Intentsx {

    var valuex = ""

    fun New(intent: Intent?) {
        valuex = ""

        if (intent != null){

            when{

                intent.getIntentValue("ntPlst") -> "" //La varible ya se asigno

                intent.getIntentValue("ntBrwsr") -> {
                    val intentNavegador = Intent(Intent.ACTION_VIEW, Uri.parse(valuex))
                    App.getMainActivity()?.startActivity(intentNavegador)
                }

                intent.getIntentValue("ntBasic") -> {
                    if (valuex != "nullx") {
                        App.getMainActivity()?.updt("A", valuex)
                    }
                }


            }

        }

    }



}