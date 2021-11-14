package com.testlabx.mashle.helpers

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.testlabx.mashle.App
import com.testlabx.mashle.BuildConfig
import com.testlabx.mashle.utils.Intentsx
import com.testlabx.mashle.utils.NetworkConnection
import com.testlabx.mashle.utils.Utilsx

object FirebaseRC {

    const val KY_VR_APP = "vrApp"
    const val KY_TYPE_UPD = "typeUpd"
    const val KY_URL_UPD = "urlUpd"
    const val KY_MSJ = "msj"
    const val KY_URL_MSJ = "urlMsj"
    const val KY_DS_NTV = "dsNtv"
    const val KY_DS_BNR = "dsBnr"
    const val KY_DS_ITR = "dsIrt"
    const val KY_DS_ADS_WB = "dsAdsWb"
    const val KY_URL_STORE = "urlStore"
    const val KY_APP_CLS = "closet"



    var updt = false
    var updtMsjSh = 0


    var vrApp = 1
    var urlUpdt = ""
    var urlStore = "https://ston-app.web.app/"
    var svLstMsj = ""

    var dsBnr = 0 //1 dsStarApp // 2 dsVungle
    var dsIrt = 0 //1 dsStarApp // 2 dsVungle
    var dsNtv = 0

    var obDt = false

    fun resetVars(){
        //IMPORTANTE
        //VERDICAR QUE ESTEN TODOS LOS VARS
        updt = false
        updtMsjSh = 0

        vrApp = 1
        urlUpdt = ""
        urlStore = "https://ston-app.web.app/"
        svLstMsj = ""

        dsBnr = 0 //1 dsStarApp // 2 dsVungle
        dsIrt = 0 //1 dsStarApp // 2 dsVungle
        dsNtv = 0

        obDt = false

    }


    fun fetch(completion: () -> Unit) {

        val remoteConfig = Firebase.remoteConfig

        /*remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("tsRmcf","issusefull")
            }
            completion()
        }*/

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                setConfigCloud()
                Log.i("TestRmt","Obtuvo isSuccessful + ${task.exception}")
                obDt = true
            }else{
                setConfigCloud()
                if (!NetworkConnection.isConnected.value){
                    obDt = false
                }else{
                    if (task.exception != null){
                        val msRmtE = task.exception.toString()
                        if (msRmtE.contains(":")){
                            val ms = msRmtE.split(":").toTypedArray()
                            FirebaseEvent.send("rmCngEr","er", ms[1])
                        }else{
                            FirebaseEvent.send("rmCngEr","er", msRmtE)
                        }

                    }
                }
                Log.i("TestRmt","Obtuvo No isSuccessful + ${task.exception}")

            }

        }



    }




    fun setConfigCloud(){
        val remoteConfig = Firebase.remoteConfig

        val vrAppx = remoteConfig.getLong(KY_VR_APP).toInt()
        val typex = remoteConfig.getString(KY_TYPE_UPD)
        val urlx = remoteConfig.getString(KY_URL_UPD)

        val msjx = remoteConfig.getString(KY_MSJ)
        val urlMsj = remoteConfig.getString(KY_URL_MSJ)

        val dsNtvx = remoteConfig.getLong(KY_DS_NTV).toInt()
        val dsBnrx = remoteConfig.getLong(KY_DS_BNR).toInt()
        val dsIrtx = remoteConfig.getLong(KY_DS_ITR).toInt()
        val dsAdsWbx = remoteConfig.getLong(KY_DS_ADS_WB).toInt()

        val urlStorex = remoteConfig.getString(KY_URL_STORE)
        val appCloset = remoteConfig.getBoolean(KY_APP_CLS)


        val plsMn1 = remoteConfig.getString("plsMn1")
        val plsMn2 = remoteConfig.getString("plsMn2")
        val plsMn3 = remoteConfig.getString("plsMn3")

        val plsMn4 = remoteConfig.getString("plsMn4")
        val plsMn5 = remoteConfig.getString("plsMn5")
        val plsMn6 = remoteConfig.getString("plsMn6")

        val plsMn7 = remoteConfig.getString("plsMn7")
        val plsMn8 = remoteConfig.getString("plsMn8")
        val plsMn9 = remoteConfig.getString("plsMn9")


        Log.i("TestRmt", "$vrAppx| $typex | $urlx | $msjx | $urlMsj | $dsNtvx | $dsBnrx | $dsIrtx | $urlStorex | $appCloset")
        Log.i("TestRmt", "Se Obtuvo")

        vrApp = vrAppx
        urlUpdt = urlx

        if (appCloset) {
            App.getMainActivity()?.finish()
        }


        //updt = true
        updtMsjSh = App.getMainActivity()?.getUpdMsSh()!!

        if (vrAppx != BuildConfig.VERSION_CODE) {
            updt = true
            if (typex == "A"){
                if (updtMsjSh != vrAppx){
                    App.getMainActivity()?.putUpdMsSh(vrAppx)
                    App.getMainActivity()?.updt(typex, urlx)
                    //put y get antes updtMsjShow
                }

            }else{
                App.getMainActivity()?.updt(typex, urlx)
            }
        }


        if (msjx != svLstMsj) {
            App.getMainActivity()?.showAcMsj(msjx,urlMsj)
        }


        if (urlStorex != ""){
            urlStore = urlStorex
        }


        //if (dsAdsWbx)
        //set ads webview



        //ADS

        // 0 -> ds Ninguno
        // 1 -> ds startApp  2 -> ds Vungle
        // 3 -> ds Ambos

        dsIrt = dsIrtx
        dsBnr = dsBnrx
        dsNtv = dsNtvx

        if (dsIrtx != 3 || dsBnrx != 3 || dsNtvx != 3){


            if (dsIrtx != 1 || dsBnrx != 1 || dsNtvx != 1) {
                App.getMainActivity()?.initStarApp()
            }

            if (dsIrtx != 2 || dsBnrx != 2 || dsNtvx != 2) {
                App.getMainActivity()?.initVungle()
            }


            if (Intentsx.valuex.contains(" /--/ ")) Utilsx.splitString(Intentsx.valuex, App.getMainActivity()?.mnList!!)

            Utilsx.getQrsFromSt(plsMn1,plsMn2,plsMn3, App.getMainActivity()?.mnList!!)
            Utilsx.getQrsFromSt(plsMn4,plsMn5,plsMn6, App.getMainActivity()?.mnList!!)
            Utilsx.getQrsFromSt(plsMn7,plsMn8,plsMn9, App.getMainActivity()?.mnList!!)
            //App.getMainActivity()?.newQuery(App.getMainActivity()?.mnList!![0].url)
            App.getMainActivity()?.addTabs()




            //when (dsBnrx){0,2 -> App.getMainActivity()?.setBnrAds() }
            //un segundo para que se inicie vungle
            //if (dsBnrx != 3) App.getMainActivity()?.setBnrAds()

            App.getMainActivity()?.initItrs(0) //test

        }


    }



    //FALTA LLEVAR VARS DEFAULT
    fun settingsRmtCnfg(){

        /*val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60//172800

        }*/ //cd 12h por default

        val firebaseConfig = Firebase.remoteConfig
        //firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.setDefaultsAsync(mapOf(
            KY_VR_APP to 1, KY_URL_UPD to "https://ston-app.web.app/", KY_TYPE_UPD to "A",
            KY_MSJ to "", KY_URL_MSJ to "",
            KY_DS_ITR to 0, KY_DS_NTV to 0, KY_DS_BNR to 0,
            KY_URL_STORE to "https://ston-app.web.app/", KY_APP_CLS to false))
    }




}