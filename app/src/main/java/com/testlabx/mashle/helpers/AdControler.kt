package com.testlabx.mashle.helpers

import android.util.Log
import android.widget.Toast
import com.testlabx.mashle.App
import com.testlabx.mashle.utils.Varss


object AdControler {

    fun showAdWeb(){

        if (Varss.pdAdWeb){

            Varss.dwCount = 0
            App.getMainActivity()?.stopTmrAdWeb()
            Varss.pdAdWeb = false
            App.getMainActivity()?.initAdWb()

            App.getMainActivity()?.showAdWeb()

        }

    }

    fun showAdNat(){
        App.getMainActivity()?.loadNativeAd()
    }

    fun showAdItr(){
        if (FirebaseRC.dsItr != 1 && FirebaseRC.dsItr != 3) {
            Varss.dwCount = 0
            App.getMainActivity()?.initStartAppInterstitial()
        }
    }
}