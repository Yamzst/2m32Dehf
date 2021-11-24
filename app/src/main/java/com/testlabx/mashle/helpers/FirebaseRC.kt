package com.testlabx.mashle.helpers

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.testlabx.mashle.App
import com.testlabx.mashle.BuildConfig
import com.testlabx.mashle.utils.Intentsx
import com.testlabx.mashle.utils.NetworkConnection
import com.testlabx.mashle.utils.Utilsx
import com.testlabx.mashle.utils.Varss
import org.schabi.newpipe.extractor.stream.StreamInfoItem

object FirebaseRC {

    const val KY_VR_APP = "vrApp"
    const val KY_TYPE_UPD = "typeUpd"
    const val KY_URL_UPD = "urlUpd"
    const val KY_MSJ = "msj"
    const val KY_URL_MSJ = "urlMsj"
    const val KY_DS_NTV = "dsNtv"
    const val KY_DS_ITR = "dsItr"
    const val KY_DS_BNR = "dsBnr"
    const val KY_DS_ADS_WB = "dsAdsWb"
    const val KY_TM_AD = "tmAd"
    const val KY_URL_STORE = "urlStore"
    const val KY_APP_CLS = "closet"


    var updt = false
    var updtMsjSh = 0

    var vrApp = 1
    var urlUpdt = ""
    var urlStore = ""
    var svLstMsj = ""

    var dsBnr = 0 //1 dsStarApp // 2 dsVungle
    var dsNtv = 0
    var dsItr = 0
    var dsAdWeb = false

    var tmAd = 5

    var obDt = false

    fun resetVars(){
        updt = false
        updtMsjSh = 0

        vrApp = 1
        urlUpdt = ""
        urlStore = ""
        svLstMsj = ""

        dsBnr = 0 //1 dsStarApp // 2 dsVungle
        dsNtv = 0
        dsItr = 0
        dsAdWeb = false

        tmAd = 5

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
                Log.i("TestRmt","No isSuccessful + ${task.exception}")

            }

        }



    }




    fun setConfigCloud(){

        val remoteConfig = Firebase.remoteConfig

        vrApp = remoteConfig.getLong(KY_VR_APP).toInt()
        val typex = remoteConfig.getString(KY_TYPE_UPD)
        urlUpdt = remoteConfig.getString(KY_URL_UPD)

        val msjx = remoteConfig.getString(KY_MSJ)
        val urlMsj = remoteConfig.getString(KY_URL_MSJ)

        dsNtv = remoteConfig.getLong(KY_DS_NTV).toInt()
        dsItr = remoteConfig.getLong(KY_DS_ITR).toInt()
        dsBnr = remoteConfig.getLong(KY_DS_BNR).toInt()
        dsAdWeb = remoteConfig.getBoolean(KY_DS_ADS_WB)

        tmAd = remoteConfig.getLong(KY_TM_AD).toInt()

        urlStore = remoteConfig.getString(KY_URL_STORE)
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


        val pgAd1 = remoteConfig.getString("pgAd1")
        val pgAd2 = remoteConfig.getString("pgAd2")
        val pgAd3 = remoteConfig.getString("pgAd3")
        val pgAd4 = remoteConfig.getString("pgAd4")
        val pgAd5 = remoteConfig.getString("pgAd5")



        if (appCloset) {
            App.getMainActivity()?.finish()
        }



        updtMsjSh = App.getMainActivity()?.getUpdMsSh()!!

        if (vrApp != BuildConfig.VERSION_CODE) {
            updt = true
            if (typex == "A"){
                if (updtMsjSh != vrApp){
                    App.getMainActivity()?.putUpdMsSh(vrApp)
                    App.getMainActivity()?.updt(typex, urlUpdt)
                }

            }else{
                App.getMainActivity()?.updt(typex, urlUpdt)
            }
        }


        if (msjx != svLstMsj) {
            App.getMainActivity()?.showAcMsj(msjx,urlMsj)
        }



        if (Intentsx.valuex.contains(" /--/ ")) Utilsx.splitString(Intentsx.valuex, Varss.mnList)


        Utilsx.getQrsFromSt(plsMn1,plsMn2,plsMn3, Varss.mnList)
        Utilsx.getQrsFromSt(plsMn4,plsMn5,plsMn6, Varss.mnList)
        Utilsx.getQrsFromSt(plsMn7,plsMn8,plsMn9, Varss.mnList)

        //ADS
        // 0 -> ds Ninguno
        // 1 -> ds startApp  2 -> ds Vungle
        // 3 -> ds Ambos


        //////////
        App.getMainActivity()?.addTabs()

        App.getMainActivity()?.adList!!.apply {
            //if (pgAd1 != "") add(pgAd1)
            if (pgAd2 != "") add(pgAd2)
            if (pgAd3 != "") add(pgAd3)
            if (pgAd4 != "") add(pgAd4)
            if (pgAd5 != "") add(pgAd5)
        }

        if (!dsAdWeb) App.getMainActivity()?.initAdWb()

        if (dsBnr != 1 || dsNtv != 1 || dsItr != 1) {
            //if se pone 3 a todos, igual se inicia
            App.getMainActivity()?.initStarApp()
        }

        if (dsBnr == 3 || dsBnr == 1) App.getMainActivity()?.hideBnr() else App.getMainActivity()?.initStarAppBanner()


    }



    //FALTA LLEVAR VARS DEFAULT
    fun settingsRmtCnfg(){

        /*val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60//172800
        }*/ //cd 12h por default

        val firebaseConfig = Firebase.remoteConfig
        //firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.setDefaultsAsync(mapOf(
            KY_VR_APP to 1, KY_TYPE_UPD to "A", KY_URL_UPD to Constants.UPD_URL,
            KY_MSJ to "", KY_URL_MSJ to "",
            KY_DS_NTV to 0, KY_DS_ITR to 0, KY_DS_BNR to 0, KY_DS_ADS_WB to false, KY_TM_AD to 5,
            KY_URL_STORE to Constants.UPD_URL, KY_APP_CLS to false))
    }




}