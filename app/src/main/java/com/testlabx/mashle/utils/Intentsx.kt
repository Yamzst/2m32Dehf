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

                intent.getIntentValue("ntPlst") -> "solo se tiene que asignar la varible y ya esta asignada"
                    //App.getMainActivity()?.searchQr(valuex)
                //v1 cambia el tab inicial al indicado para la plst
                //v2 añade un nuevo tab y reproduce la plst de ese

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